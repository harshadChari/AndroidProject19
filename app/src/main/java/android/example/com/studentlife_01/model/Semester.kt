package android.example.com.studentlife_01.model

class Semester(sem:String,end_date:String,start_date:String) {
        var id:String
        var sem:String
        var start_date:String
        var end_date:String

        init {
            this.sem = sem
            this.start_date = start_date
            this.end_date = start_date
            this.id = ""
        }

    }
