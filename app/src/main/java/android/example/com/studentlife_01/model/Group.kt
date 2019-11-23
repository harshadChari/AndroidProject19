package android.example.com.studentlife_01.model

class Group(name:String) {
    var id:String
    var name:String
    var created_at:String
    var user_id:String
    var admin_state:String

    init {
        this.name = name
        this.created_at = ""
        this.id = ""
        this.user_id = ""
        this.admin_state = ""
    }
}