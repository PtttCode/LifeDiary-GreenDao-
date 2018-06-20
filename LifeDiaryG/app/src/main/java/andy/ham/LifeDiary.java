package andy.ham;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.reflect.Field;

import andy.ham.DaoMaster.DevOpenHelper;
import andy.ham.pageView.MainAcitivity;

//继承来自listView
public class LifeDiary extends ListActivity {
	// 检查密码
	private SharedPreferences textpassword = null;
	private String password = null;
	private boolean isSet = false;
	private EditText checkpass = null;
	// 检查是否第一次进入应用
	boolean isFirstIn = false;
	// 插入一条新纪录
	public static final int MENU_ITEM_INSERT = Menu.FIRST;
	// 编辑内容
	public static final int MENU_ITEM_EDIT = Menu.FIRST + 1;
	// private static final String[] PROJECTION =
	// new String[] { DiaryColumns._ID,
	// DiaryColumns.TITLE, DiaryColumns.CREATED };
	//
	// GreenDAO使用的变量
	private SQLiteDatabase db;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao noteDao;
	private Note note;
	private ImageButton floatMenu;
	private PopupMenu popupMenu;


	private Cursor cursor;
	private static int rows;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_list);

		popupMenu=new PopupMenu(this,findViewById(R.id.floatMenu));

		Menu menu=popupMenu.getMenu();
		MenuInflater menuInflater=getMenuInflater();
		menuInflater.inflate(R.menu.floatmenu,menu);

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch (menuItem.getItemId())
				{
					case R.id.account:
						Intent intent=new Intent(LifeDiary.this, MainAcitivity.class);
						startActivity(intent);
						break;
					case R.id.deleteAll:
						noteDao.deleteAll();
						final AlertDialog.Builder confirmDelete = new AlertDialog.Builder(LifeDiary.this);
						confirmDelete.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								noteDao.deleteAll();
								InitList();
							}
						});

						confirmDelete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
							}
						});
						confirmDelete.setMessage("确定要全部删除吗?");
						confirmDelete.setTitle("警告");
						confirmDelete.show();
						break;
					case R.id.news:
						Intent intent0 = new Intent(LifeDiary.this, DiaryEditor.class);
						intent0.setAction(DiaryEditor.INSERT_DIARY_ACTION);
						intent0.setData(getIntent().getData());
						startActivity(intent0);
						overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
						cursor.requery();
						break;
					case R.id.paste:
						finish();
						overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
						break;
					default:
						break;
				}
				return false;
			}
		});

		// 初始化GreenDAO
		InitDAO();

		// 初始化List
		InitList();

		final AlertDialog.Builder confirmDelete = new AlertDialog.Builder(this);
		this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
				Log.e("tag", "confirm " + pos + id);
				final long mid = id;
				confirmDelete.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						noteDao.deleteByKey(mid);
						InitList();
					}
				});

				confirmDelete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
				confirmDelete.setMessage("确定要删除吗?");
				confirmDelete.setTitle("警告");
				confirmDelete.show();
				return true;
			}
		});

	}

	/*
	 * 初始化DAO
	 */
	private void InitDAO() {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		noteDao = daoSession.getNoteDao();

	}

	/*
	 * 初始化List
	 */
	private void InitList() {
		String textColumn = NoteDao.Properties.Title.columnName;
		String dateColunm = NoteDao.Properties.Date.columnName;
		String addressColunm = NoteDao.Properties.Address.columnName;
		String bodyColunm=NoteDao.Properties.Body.columnName;
		Log.e("tag", textColumn + NoteDao.Properties.Address);
		Log.e("tag", bodyColunm );
		String orderBy = dateColunm + " COLLATE LOCALIZED DESC";
		cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(),
				null, null, null, null, orderBy);
		String[] from = {textColumn, dateColunm, addressColunm,bodyColunm};
		int[] to = {R.id.text1, R.id.created, R.id.addrss,R.id.body};

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.diary_row, cursor, from, to);
		setListAdapter(adapter);
		rows = cursor.getCount();
		Log.e("tag", "行数" + cursor.getCount());
	}

	// 添加选择菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_ITEM_INSERT, 0, R.string.menu_insert);
		return true;
	}

	// 添加选择菜单的选择事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// 插入一条数据
			case MENU_ITEM_INSERT:
				Intent intent0 = new Intent(this, DiaryEditor.class);
				intent0.setAction(DiaryEditor.INSERT_DIARY_ACTION);
				intent0.setData(getIntent().getData());
				overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
				startActivity(intent0);
				cursor.requery();
				return true;

		}
		return super.onOptionsItemSelected(item);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		String mid = Long.toString(id);
		Log.d("id", mid);
		// Intent intent = new Intent();
		// startActivity(new Intent(DiaryEditor.EDIT_DIARY_ACTION, uri));
		Intent intent = new Intent(this, DiaryEditor.class);
		intent.setAction(DiaryEditor.EDIT_DIARY_ACTION);
		Bundle bundle = new Bundle();
		bundle.putLong("id", id);
		intent.putExtras(bundle);
		Log.d("id", mid);
		startActivity(intent);
		cursor.requery();
	}


	protected void onActivityResult(int requestCode, int resultCode,
									Intent intent) {
		cursor.requery();
		super.onActivityResult(requestCode, resultCode, intent);
		// renderListView();
	}


	@SuppressLint("RestrictedApi")
	public void popupmenu(View v) {
		popupMenu.show();
	}


	public static int getRows()
	{
		return rows;
	}
}


	// @SuppressWarnings("deprecation")
	// private void renderListView() {
	// Cursor cursor = managedQuery(getIntent().getData(), PROJECTION,
	// null,null, DiaryColumns.DEFAULT_SORT_ORDER);
	//
	// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
	// R.layout.diary_row, cursor, new String[] { DiaryColumns.TITLE,
	// DiaryColumns.CREATED }, new int[] { R.id.text1,R.id.created });
	// setListAdapter(adapter);
	// }





