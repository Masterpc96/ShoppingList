package com.michaelsSoftware.ShoppingList.dataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.michaelsSoftware.ShoppingList.dao.MyDao;
import com.michaelsSoftware.ShoppingList.entity.Product;


@Database(entities = {Product.class}, version = 18)
public abstract class DataBase extends RoomDatabase {
    private static final String DB_NAME = "shoppingList.db";
    private static DataBase instance;

    public abstract MyDao getDao();

    public static synchronized DataBase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static DataBase create(final Context context) {
        return Room.databaseBuilder(
                context,
                DataBase.class,
                DB_NAME).allowMainThreadQueries().build();
    }
}
