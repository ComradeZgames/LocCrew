package ru.comradez.loccrew;


public class DBRecordBuilder {

    private final int id;
    private String start = null, finish = null;

    public DBRecordBuilder(int id) {
        this.id = id;

    }
        public void add (DBHelper db , int checkedId, String time, boolean isStart){
        if(checkedId == id) {
            if (isStart)
                start = time;
            else finish = time;
        }
            if (isComplete()) {
                db.AddRecord(returnResult());
            }
        }

        public boolean respondForId (int id){
            return this.id == id;
        }

    private boolean isComplete () {
        return (start != null) && (finish != null);
    }
            private DateTimeString returnResult () {
                return new DateTimeString(id, start, finish);
            }
}

