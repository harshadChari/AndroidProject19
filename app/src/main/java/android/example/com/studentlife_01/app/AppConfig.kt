package android.example.com.studentlife_01.app

object AppConfig {
   val SERVER = "192.168.43.19"
   // val SERVER = "192.168.56.1"

    // Server user login url
    val URL_LOGIN = "http://" + SERVER + "/studentlife_api1/login.php"

    // Server user register url
    val URL_REGISTER = "http://" + SERVER + "/studentlife_api1/register.php"

   // get notices
    val URL_GET_ALL_NOTICES = "http://" + SERVER + "//studentlife_api/api/notices/read.php?user_id="

    // get notices
    val URL_GET_NOTICES_BY_GROUP = "http://" + SERVER + "//studentlife_api/api/notices/read_by_group.php?group_id="

    // create notices
    val URL_CREATE_NOTICE = "http://" + SERVER + "///studentlife_api/api/notices/create.php"

    val URL_ADD_USER = "http://"+ SERVER + "/studentlife_api/api/groups/members/create.php"

    //  post notices
    val URL_POST_NOTICE = "http://" + SERVER + "///studentlife_api/api/notices/post.php"

    // delete notices
    val URL_DELETE_NOTICE = "http://" + SERVER + "//studentlife_api/api/notices/delete.php?id="

    // delete notices
    val URL_GET_GROUPS_BY_USER = "http://" + SERVER + "//studentlife_api/api/groups/read.php?user_id="

    // create group
    val URL_CREATE_GROUP = "http://" + SERVER + "///studentlife_api/api/groups/create.php"// create group


    val ROOT_URL  = "http://" + SERVER + "///studentlife_api/api/Api.php?apicall="// create group
    val UPLOAD_URL2  = ROOT_URL + "uploadpic"
    val GET_PICS_URL  = ROOT_URL + "getpics"


    val UPLOAD_URL = "http://" + SERVER + "///studentlife_api/api/notices/upload.php"
    val IMAGES_URL = "http://192.168.94.1/AndroidImageUpload/getImages.php"



    //get semester
    val URL_CREATE_SEMESTER = "http://192.168.43.19///studentlife_api/api/semesters/create.php"

    // get notices
    val URL_GET_ALL_SEMESTERS = "http://192.168.43.19//studentlife_api/api/semesters/readby_user_id.php?user_id="




}