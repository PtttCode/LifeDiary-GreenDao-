package andy.ham;

import java.util.Calendar;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import andy.ham.Fields.DiaryColumns;
public class LifeDiaryContentProvider extends ContentProvider{
	//定义一：日记本中变量
	private static final String DATABASE_NAME = "database";
	private static final int DATABASE_VERSION = 3;
	private static final String DIARY_TABLE_NAME = "diary";
	private static final int DIARIES = 1;
	private static final int DIARY_ID = 2;
	//定义二：UriMatcher。用来匹配URI的类型，是单一的数据请求还是全部数据请求
	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Fields.AUTHORITY, "diaries", DIARIES);
		sUriMatcher.addURI(Fields.AUTHORITY, "diaries/#", DIARY_ID);

	}
	/*定义三：DatabaseHelper。DatabaseHelper是继承SQLiteOpenHelper的
	 * SQLiteOpenHelper是一个抽象类，有3个函数 onCreate、onUpdate、onOpen
	*/
	private DatabaseHelper mOpenHelper;
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			Log.i("jinyan", "DATABASE_VERSION=" + DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("jinyan", "onCreate(SQLiteDatabase db)");
			String sql = "CREATE TABLE " + DIARY_TABLE_NAME + " ("
					+ DiaryColumns._ID + " INTEGER PRIMARY KEY,"
					+ DiaryColumns.TITLE + " TEXT," + DiaryColumns.BODY
					+ " TEXT," + DiaryColumns.CREATED + " TEXT" + ");";
			//
			sql ="CREATE TABLE " + DIARY_TABLE_NAME + " ("
					+ DiaryColumns._ID + " INTEGER PRIMARY KEY,"
					+ DiaryColumns.TITLE + " varchar(255)," + DiaryColumns.BODY
					+ " TEXT," + DiaryColumns.CREATED + " TEXT" + ");";
			//
			Log.i("jinyan", "sql="+sql);
			db.execSQL(sql);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("jinyan",
					" onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)="
							+ newVersion);
			db.execSQL("DROP TABLE IF EXISTS diary");
			onCreate(db);

		}

	}
	//定义四：getFormateCreatedDate。一个获取当前时间的函数
	public static String getFormateCreatedDate() {
		Calendar calendar = Calendar.getInstance();
		String created = calendar.get(Calendar.YEAR) + "年"
				+ calendar.get(Calendar.MONTH) + "月"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "日"
				+ calendar.get(Calendar.HOUR_OF_DAY) + "时"
				+ calendar.get(Calendar.MINUTE) + "分";
		return created;
	}


	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		//SQLiteQueryBuilder是一个构造sql查询语句的辅助类
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		//UriMatcher.match(uri)根据返回值可以判断这次查询请求时
		//他是请求的全部数据还是某个id的数据
		switch (sUriMatcher.match(uri)) {
			//如果请求是DIARIES只需执行setTables(DIARY_TABLE_NAME)
			case DIARIES:
				qb.setTables(DIARY_TABLE_NAME);
				break;
			//如果返回值是DIARY_ID，还需要加入where条件
			case DIARY_ID:
				qb.setTables(DIARY_TABLE_NAME);
				qb.appendWhere(DiaryColumns._ID + "="
						+ uri.getPathSegments().get(1));
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Fields.DiaryColumns.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		//关键的查询语句，query（）参数为：
		//数据库实例、字符串数组、where部分
		//以及字符串数组里面每一项依次代替第三个参数出现问号
		//null表示sql语句groupby
		//第二个null表示sql语句having部分、最后一个参数是排序
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs
				, null,null, orderBy);
		return c;
	}

	//返回一个给Uri制定数据的MIME类型
	//他的返回值如果是以vnd.android.cursor.item开头，那么Uri是单条数据
	//如果是以vnd.android.cursor.dir开头的，说明Uri指定的全部数据
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case DIARIES:
				return DiaryColumns.CONTENT_TYPE;

			case DIARY_ID:
				return DiaryColumns.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		//判断uri，如果这个uri不是DIARIES类型的，那么这个uri就是一个非法的uri
		if (sUriMatcher.match(uri) != DIARIES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		if (values.containsKey(Fields.DiaryColumns.CREATED) == false) {
			values.put(Fields.DiaryColumns.CREATED, getFormateCreatedDate());
		}

		if (values.containsKey(Fields.DiaryColumns.TITLE) == false) {
			Resources r = Resources.getSystem();
			values.put(Fields.DiaryColumns.TITLE, r
					.getString(android.R.string.untitled));
		}

		if (values.containsKey(Fields.DiaryColumns.BODY) == false) {
			values.put(Fields.DiaryColumns.BODY, "");
		}
		//得到一个SQLiteDatabase的实例
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		//负责插入一条记录到数据库当中
		long rowId = db.insert
				(DIARY_TABLE_NAME, Fields.DiaryColumns.BODY, values);
		if (rowId > 0) {
			Uri diaryUri = ContentUris.withAppendedId(
					Fields.DiaryColumns.CONTENT_URI, rowId);
			//注意：insert（）返回的是一个uri，而不是一个记录id
			return diaryUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		//getWritableDatabase（）方法得到一个string的list
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		//得到rowId的值，如果get（0）那值就为diaries
		String rowId = uri.getPathSegments().get(1);
		//标准的SQLite删除操作，第一个参数是数据表的名字
		//第二个相当于SQL语句中的where部分。
		return db.delete(DIARY_TABLE_NAME, DiaryColumns._ID +
				"=" + rowId, null);
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
					  String[] whereArgs) {
		//getWritableDatabase（）方法得到SQLiteDatabase的实例
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		//得到rowId的值
		String rowId = uri.getPathSegments().get(1);
		//最后在调用update语句执行更新操作
		return db.update(DIARY_TABLE_NAME, values, DiaryColumns._ID +
				"="+ rowId, null);
	}

}
