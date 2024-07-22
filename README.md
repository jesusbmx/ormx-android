## ORMX

Convertir datos entre el sistema de tipos utilizado en un lenguaje de programación orientado a objetos

## Ejemplos

Simple CRUD

```java
@TableInfo(name = "tb_nota")
public class Nota {

  // Variables

  @ColumnInfo(primaryKey = true, autoIncrement = true)
  public long id;

  @ColumnInfo
  public long fecha;

  @ColumnInfo
  public String texto;

  // Constructor

  public Nota() {
    // TODO Auto-generated constructor stub
  }

  // Funciones 

  /**
   * @return una lista del recurso.
   */
  public static List<Nota> getAll() {
    DB db = DB.db;
    try {
      return db.queryBuilder()
        .order_by("fecha", "desc")
        .get_list(Nota.class);
    } finally {
      db.close();
    }
  }

  /**
   * Guarda el registro.
   * 
   * @param o modelo
   * 
   * @return boolean <b>TRUE</b> exito <b>FALSE</b> fallo
   */
  public static boolean save(Nota o) {
    return (o.id == 0) ? insert(o) : update(o);
  }

  /**
   * Inserta un registro en la db.
   * 
   * @param o modelo
   * 
   * @return boolean <b>TRUE</b> exito <b>FALSE</b> fallo
   */
  public static boolean insert(Nota o) {
    DB db = DB.db;
    try {
      return db.dao(Nota.class)
        .insert(o);
    } finally {
      db.close();
    }
  }

  /**
   * Actualiza un registro en la db.
   * 
   * @param o modelo
   * 
   * @return boolean <b>TRUE</b> exito <b>FALSE</b> fallo
   */
  public static boolean update(Nota o) {
    DB db = DB.db;
    try {
      return db.dao(Nota.class)
        .update(o) > 0;
    } finally {
      db.close();
    }
  }

  /**
   * Elimina un registro.
   * 
   * @param id identificador
   *
   * @return boolean <b>TRUE</b> exito <b>FALSE</b> fallo
   */
  public static boolean delete(long id) {
    DB db = DB.db;
    try {
      return db.dao(Nota.class)
        .deleteById(id) > 0;
    } finally {
      db.close();
    }
  }

  public static OrmIterator<Nota> search(String q, Date date) throws SQLException {
    DB db = DB.db;
    OrmResultSet result = db.dao(Nota.class).queryBuilder()
      //.select("*")
      //.distinct()
      //.from("tb_nota")
      .where("CAST(fecha AS DATE)", "=", date.getTime())
      .like("texto", q)
      //.where_in("id", 50, 60, 30)
      //.or_where_in("id", 22, 44)
      //.where("id", ">", 14)
      //.where_not_in("id", 50, 60, 30)
      //.or_where_not_in("id", 22, 44)
      //.not_like("texto", "jet")
      //.join("tb_nota b", "tb_nota.id = b.id")
      //.join("tb_nota c", "tb_nota.id = c.id", "LEFT")
      .order_by("fecha", "DESC").order_by("id", "DESC")
      //.group_by("tb_nota.id")
      //.group_by("tb_nota.texto")
      //.having("tb_nota = 'Hello world'")
      //.limit(7, 25)
    ;
    
    return result.it(Nota.class);
  }
}

```

### Config database

```java
public class DB extends OrmDataBase {

  // Constantes

  public static final String NAME = "notas.db";
  public static final int VERSION = 1;

  public static DB db = null;

  // Constructor

  private DB(Context context) {
    super(context, NAME, null, VERSION);
  }

  // Funciones

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE tb_nota ("
      + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
      + "fecha INTEGER, "
      + "texto TEXT)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < newVersion) {
      db.execSQL("DROP TABLE IF EXISTS tb_nota");
      this.onCreate(db);
    }
  }

  public static void init(Context applicationContext) {
    db = new DB(applicationContext);
    db.getWritableDatabase();
  }
}
```

```java
DB.init(getApplicationContext());
```

License
=======

    Copyright 2020 JesusBetaX, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
