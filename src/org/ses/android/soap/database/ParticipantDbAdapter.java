/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ses.android.soap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple Participant database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all Participants as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class ParticipantDbAdapter {

    public static final String KEY_NOMBRES = "Nombres";
    public static final String KEY_APELLIDOPATERNO = "ApellidoPaterno";
    public static final String KEY_APELLIDOMATERNO = "ApellidoMaterno";    
    public static final String KEY_CODIGOTIPODOCUMENTO = "CodigoTipoDocumento";
    public static final String KEY_DOCUMENTOIDENTIDAD = "DocumentoIdentidad";
    public static final String KEY_FECHANACIMIENTO = "FechaNacimiento";
    public static final String KEY_SEXO = "Sexo";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "ParticipantDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table participant (_id integer primary key autoincrement, "
        + "Nombres text not null, ApellidoPaterno text not null, ApellidoMaterno text not null,CodigoTipoDocumento integer, DocumentoIdentidad text not null, FechaNacimiento text not null, Sexo integer);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "participant";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS participant");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ParticipantDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the Participant database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ParticipantDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    
    public long createParticipant(String Nombres,
    		String ApellidoPaterno,
    		String ApellidoMaterno ,
    		int CodigoTipoDocumento,
    		String DocumentoIdentidad,
    		String FechaNacimiento,
    		int Sexo) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NOMBRES, Nombres);
        initialValues.put(KEY_APELLIDOPATERNO,ApellidoPaterno );
        initialValues.put(KEY_APELLIDOMATERNO,ApellidoMaterno );
        initialValues.put(KEY_CODIGOTIPODOCUMENTO,CodigoTipoDocumento );
        initialValues.put(KEY_DOCUMENTOIDENTIDAD, DocumentoIdentidad);
        initialValues.put(KEY_FECHANACIMIENTO,FechaNacimiento );
        initialValues.put(KEY_SEXO, Sexo);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteParticipant(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all Participant in the database
     * 
     * @return Cursor over all Participant
     */
    public Cursor fetchAllParticipant() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, 
        		KEY_NOMBRES,
        		KEY_APELLIDOPATERNO,
        		KEY_APELLIDOMATERNO,
        		KEY_CODIGOTIPODOCUMENTO,
        		KEY_DOCUMENTOIDENTIDAD,
        		KEY_FECHANACIMIENTO,
        		KEY_SEXO
        }, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchParticipant(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            		KEY_NOMBRES,
            		KEY_APELLIDOPATERNO,
            		KEY_APELLIDOMATERNO,
            		KEY_CODIGOTIPODOCUMENTO,
            		KEY_DOCUMENTOIDENTIDAD,
            		KEY_FECHANACIMIENTO,
            		KEY_SEXO            
            }, KEY_ROWID + "=" + rowId, null,null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateParticipant(long rowId, 
    		String Nombres,
    		String ApellidoPaterno,
    		String ApellidoMaterno ,
    		int CodigoTipoDocumento,
    		String DocumentoIdentidad,
    		String FechaNacimiento,
    		int Sexo) {
        
    	ContentValues args = new ContentValues();

        args.put(KEY_NOMBRES, Nombres);
        args.put(KEY_APELLIDOPATERNO,ApellidoPaterno );
        args.put(KEY_APELLIDOMATERNO,ApellidoMaterno );
        args.put(KEY_CODIGOTIPODOCUMENTO,CodigoTipoDocumento );
        args.put(KEY_DOCUMENTOIDENTIDAD, DocumentoIdentidad);
        args.put(KEY_FECHANACIMIENTO,FechaNacimiento );
        args.put(KEY_SEXO, Sexo);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
