package andy.ham;

import java.text.DateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;

import andy.ham.DaoMaster.DevOpenHelper;

public class DiaryEditor extends Activity {

	private static final String TAG = "Diary";
	public static final String EDIT_DIARY_ACTION = "andy.ham.DiaryEditor.EDIT_DIARY";
	public static final String INSERT_DIARY_ACTION = "andy.ham.DiaryEditor.action.INSERT_DIARY";


	AMapLocationClient mLocationClient=null;
	GetLocation getLocation=new GetLocation(mLocationClient);
	/**
	 * 查询cursor时候，感兴趣的那些条例。
	 */
	// private static final String[] PROJECTION
	// = new String[] { DiaryColumns._ID, // 0
	// DiaryColumns.TITLE, DiaryColumns.BODY, // 1
	// };

	// GreenDAO使用的变量
	private SQLiteDatabase db;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao noteDao;
	private Note note;
	private Cursor cursor;

	private static final int STATE_EDIT = 0;
	private static final int STATE_INSERT = 1;
	private String dUriString;
	private int mState;
	private Uri mUri;
	private Cursor mCursor;
	private EditText mTitleText;
	private EditText mBodyText;
	private ImageButton confirmButton;
	private ImageButton resetButton;
	private long mid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setTheme(android.R.style.Theme_Black);
		final Intent intent = getIntent();
		final String action = intent.getAction();
		ActionBar actionBar = getActionBar();
		if(actionBar != null){
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		setContentView(R.layout.diary_edit);

		mTitleText = findViewById(R.id.title);
		mBodyText = findViewById(R.id.body);
		confirmButton = findViewById(R.id.confirm);
		resetButton=findViewById(R.id.reset);


		initDAO();
		Log.d("action", action);

		if (EDIT_DIARY_ACTION.equals(action)) {// 编辑日记
			// mState = STATE_EDIT;
			// mUri = intent.getData();
			// dUriString = mUri.toString();
			// mCursor = managedQuery(mUri, PROJECTION, null, null, null);
			// mCursor.moveToFirst();
			// String title = mCursor.getString(1);
			// mTitleText.setTextKeepState(title);
			// String body = mCursor.getString(2);
			// mBodyText.setTextKeepState(body);
			Bundle bundle = this.getIntent().getExtras();
			mid = bundle.getLong("id");
			Log.e("tag","mid:"+mid);
			// note = noteDao.load(mid);
			note = noteDao.loadByRowId(mid);

			mTitleText.setTextKeepState(note.getTitle());
			mBodyText.setTextKeepState(note.getBody());

			// setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));
			setTitle("编辑日记");
			getLocation();
		} else if (INSERT_DIARY_ACTION.equals(action)) {// 新建日记
			mState = STATE_INSERT;
			setTitle("新建日记");
			getLocation();
		} else {
			Log.e(TAG, "no such action error");
			finish();
			return;
		}
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (mState == STATE_INSERT) {
					insertDiary();
				} else {
					updateDiary();
				}
				Intent intent = new Intent(DiaryEditor.this, LifeDiary.class);
				startActivity(intent);
				DiaryEditor.this.finish();
			}
		});
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mTitleText.setText("");
				mBodyText.setText("");
			}
		});
	}

	private void getLocation()
	{
		getLocation.mLocationClient=new AMapLocationClient(getApplicationContext());
		getLocation.init();
	}

	private void initDAO() {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		noteDao = daoSession.getNoteDao();
	}

	private void insertDiary() {
		//生成随机数id
		long id = (int) ((Math.random() * 9 + 1) * 100000);
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		String address=getLocation.pipe();
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM);
		String date = df.format(new Date());
		int num=body.length()+getData();
		SaveFile(num);


		Note note = new Note(id, title, body, date,address);
		Log.e("tag",address+"?");
		noteDao.insert(note);

		Log.d("DaoExample", "Inserted new note, ID: " + note.getId());
		// ContentValues values = new ContentValues();
		// values.put(Fields.DiaryColumns.CREATED, LifeDiaryContentProvider
		// .getFormateCreatedDate());
		// values.put(Fields.DiaryColumns.TITLE, title);
		// values.put(Fields.DiaryColumns.BODY, body);
		// getContentResolver().
		// insert(Fields.DiaryColumns.CONTENT_URI, values);

	}

	private void updateDiary() {
		long id=note.getId();
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		String address=getLocation.pipe();
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM);
		String date = df.format(new Date());
		int num=body.length()+getData();
		SaveFile(num);
		Note updateNote=new Note(id,title,body,date,address);
		Log.e("tag",address+"?");
		noteDao.insertOrReplace(updateNote);

		// ContentValues values = new ContentValues();
		// values.put(Fields.DiaryColumns.CREATED, LifeDiaryContentProvider
		// .getFormateCreatedDate());
		// values.put(Fields.DiaryColumns.TITLE, title);
		// values.put(Fields.DiaryColumns.BODY, body);
		// getContentResolver().
		// update(mUri, values,null, null);
	}

	// 添加菜单键选项
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.delete, menu);
		return true;
	}

	// 添加选择菜单的选择事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// 删除当前数据
			case R.id.delete:
				// getContentResolver().delete(dUri, null, null);
				noteDao.deleteByKey(mid);

				Toast.makeText(DiaryEditor.this, R.string.diary_delete_success,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(DiaryEditor.this, LifeDiary.class);
				startActivity(intent);
				DiaryEditor.this.finish();
				break;
			case android.R.id.home:
				DiaryEditor.this.finish();
				return true;
			case R.id.save:
				if (mState == STATE_INSERT) {
					insertDiary();
				} else {
					updateDiary();
				}
				Intent intent0 = new Intent(DiaryEditor.this, LifeDiary.class);
				startActivity(intent0);
				DiaryEditor.this.finish();
				break;
			}
		return super.onOptionsItemSelected(item);
	}

	private void SaveFile(int num)
	{
		String totalNum=Integer.toString(num);
		boolean isSave=SaveFileService.saveFile(this,totalNum);
		if(isSave)	Log.e("tag","保存成功"+num);
		else	Toast.makeText(this,"保存失败",Toast.LENGTH_LONG).show();
	}
	private int getData()
	{
		int a=SaveFileService.getUserInfo(this);
		Log.e("tag","回显数据"+a);
		return a;
	}
}
