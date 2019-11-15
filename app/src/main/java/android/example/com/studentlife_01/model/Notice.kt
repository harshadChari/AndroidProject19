package android.example.com.studentlife_01.model

class Notice(title:String,content:String) {
    var id:String
    var title:String
    var content:String

    init {
        this.title = title
        this.content = content
        this.id = ""
    }

}