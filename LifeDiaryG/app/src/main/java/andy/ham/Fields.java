package andy.ham;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Fields {
    //这里的 AUTHORITY 要求是唯一，而且和Manifest当中provider标签的AUTHORITY内容一致
    //声明了URI的授权者名称
    public static final String AUTHORITY = "andy.ham.diarycontentprovider";
    //构造方法
    private Fields() {}
    public static final class DiaryColumns implements BaseColumns {
        // 内部静态类，和它的名字意思一样定义了列表字段的名字
        private DiaryColumns() {}
        //正式声明了contentURI
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/diaries");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.diary";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.diary";

        public static final String DEFAULT_SORT_ORDER = "created DESC";

        public static final String TITLE = "title";

        public static final String BODY = "body";

        public static final String CREATED = "created";
    }
}

