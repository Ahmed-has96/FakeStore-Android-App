package com.example.fakestore.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fakestore.model.ProductModelItem
import com.example.fakestore.model.Rating


class ProduktSQLiteHjelper(var context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "product_database"
        const val DATABASE_VERSION = 1

        const val TABLE_PRODUCTS = "products"
        const val TABLE_FAVOURITES = "fav_product_table"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_PRICE = "price"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_RATE = "rate"
        const val COLUMN_COUNT = "count"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_TYPE = "is_fav"

        private const val TABLE_NAME = "history"
        private const val KEY_ID = "id"
        private const val KEY_CREATED = "created"
        private const val KEY_PRODUCTS_IDS = "productsIds"
        private const val KEY_PRICE = "price"
    }

    private val createTableQuery = """
        CREATE TABLE $TABLE_PRODUCTS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_TITLE TEXT,
            $COLUMN_PRICE REAL,
            $COLUMN_DESCRIPTION TEXT,
            $COLUMN_CATEGORY TEXT,
            $COLUMN_IMAGE TEXT,
            $COLUMN_RATE REAL,
            $COLUMN_QUANTITY INTEGER,
            $COLUMN_COUNT INTEGER
        )
    """.trimIndent()
    private val createTableFav = """
        CREATE TABLE $TABLE_FAVOURITES (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_TITLE TEXT,
            $COLUMN_PRICE REAL,
            $COLUMN_DESCRIPTION TEXT,
            $COLUMN_CATEGORY TEXT,
            $COLUMN_IMAGE TEXT,
            $COLUMN_RATE REAL,
            $COLUMN_QUANTITY INTEGER,
            $COLUMN_COUNT INTEGER,
            $COLUMN_TYPE INTEGER
        )
    """.trimIndent()

    private val upgradeTableQuery = """
        DROP TABLE IF EXISTS $TABLE_PRODUCTS;
        $createTableQuery
    """.trimIndent()

    private val upgradeTableFavQuery = """
        DROP TABLE IF EXISTS $TABLE_PRODUCTS;
        $createTableQuery
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableQuery)

        val CREATE_TABLE =
            ("CREATE TABLE $TABLE_NAME($KEY_ID INTEGER PRIMARY KEY, $KEY_CREATED INTEGER, $KEY_PRODUCTS_IDS TEXT, $KEY_PRICE TEXT)")
        db.execSQL(CREATE_TABLE)
        db.execSQL(createTableFav)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Typically, you would handle migration of data if the schema changes
        // For simplicity, we are dropping the existing table and recreating it
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

        db.execSQL(upgradeTableQuery)
        db.execSQL(upgradeTableFavQuery)
    }

    // CREATE
    fun insertProduct(product: ProduktEntitet): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID,product.id)
            put(COLUMN_TITLE, product.title)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_DESCRIPTION, product.description)
            put(COLUMN_CATEGORY, product.category)
            put(COLUMN_IMAGE, product.image)
            put(COLUMN_RATE, product.rate)
            put(COLUMN_COUNT, product.count)
            put(COLUMN_QUANTITY, product.quantity)
        }
        val newRowId = db.insert(TABLE_PRODUCTS, null, values)
        db.close()
        return newRowId
    }

    fun addToFav(product: ProduktEntitet): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(COLUMN_TYPE, 1) // Assuming 1 represents true, change it to 0 for false if needed

        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(product.id.toString())

        val rowsAffected = db.update(TABLE_FAVOURITES, values, whereClause, whereArgs)

        return rowsAffected > 0
    }


    fun addProductOffline(product: ProduktEntitet): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, product.id)
            put(COLUMN_TITLE, product.title)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_DESCRIPTION, product.description)
            put(COLUMN_CATEGORY, product.category)
            put(COLUMN_IMAGE, product.image)
            put(COLUMN_RATE, product.rate)
            put(COLUMN_COUNT, product.count)
            put(COLUMN_QUANTITY, product.quantity)
            put(COLUMN_TYPE, false)
        }
        val newRowId = db.insert(TABLE_FAVOURITES, null, values)
        db.close()
        return newRowId
    }

    // READ
    fun getAllProducts(): List<ProduktEntitet> {
        val products = mutableListOf<ProduktEntitet>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val product = ProduktEntitet(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
            )
            products.add(product)
        }

        cursor.close()
        db.close()
        return products
    }

    fun getAllfAVProducts(isFav: Boolean): List<ProduktEntitet> {
        val products = mutableListOf<ProduktEntitet>()
        val db = readableDatabase
        val selection =
            "$COLUMN_TYPE = ?" // assuming COLUMN_TYPE is the name of your boolean column

        val selectionArgs = if (isFav) arrayOf("1") else arrayOf("0")
        val cursor: Cursor =
            db.query(TABLE_FAVOURITES, null, selection, selectionArgs, null, null, null)

        while (cursor.moveToNext()) {
            val product = ProduktEntitet(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
            )
            products.add(product)
        }

        cursor.close()
        db.close()
        return products
    }

    fun getAllOfflineProducts(): List<ProductModelItem> {
        val products = mutableListOf<ProductModelItem>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_FAVOURITES, null, null, null, null, null, null)


        while (cursor.moveToNext()) {
            val product = ProductModelItem(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                Rating(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RATE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COUNT))
                ),

                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            )

            products.add(product)
        }

        cursor.close()
        db.close()
        return products
    }

    fun getProductById(productId: Long): ProduktEntitet? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_FAVOURITES,
            arrayOf(
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_PRICE,
                COLUMN_DESCRIPTION,
                COLUMN_CATEGORY,
                COLUMN_IMAGE,
                COLUMN_RATE,
                COLUMN_COUNT,
                COLUMN_QUANTITY
            ),
            "$COLUMN_ID = ?",
            arrayOf(productId.toString()),
            null,
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val product = ProduktEntitet(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
            )
            cursor.close()
            product
        } else {
            null
        }
    }


    // DELETE
    fun deleteProduct(productId: Long): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_PRODUCTS, "$COLUMN_ID = ?", arrayOf(productId.toString()))
        db.close()
        return rowsDeleted
    }

    fun removefromFav(productId: Long): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(COLUMN_TYPE, 0) // Assuming 1 represents true, change it to 0 for false if needed

        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(productId.toString())

        val rowsAffected = db.update(TABLE_FAVOURITES, values, whereClause, whereArgs)

        return rowsAffected > 0
    }

    fun deleteAllProducts() {
        val db = writableDatabase
        db.delete(TABLE_PRODUCTS, null, null)
        db.close()
    }

    fun addHistory(history: HistorikkEntitet) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_ID, history.id)
        values.put(KEY_CREATED, history.created)
        values.put(KEY_PRODUCTS_IDS, history.productsIds)
        values.put(KEY_PRICE, history.price)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getHistory(id: Long): HistorikkEntitet? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(KEY_ID, KEY_CREATED, KEY_PRODUCTS_IDS, KEY_PRICE),
            "$KEY_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val history = HistorikkEntitet(
                cursor.getLong(0),
                cursor.getLong(1),
                cursor.getString(2),
                cursor.getString(3)
            )
            cursor.close()
            history
        } else {
            null
        }
    }

    fun getAllHistory(): List<HistorikkEntitet> {
        val historyList = ArrayList<HistorikkEntitet>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val history = HistorikkEntitet(
                    cursor.getLong(0),
                    cursor.getLong(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
                historyList.add(history)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return historyList
    }

}
