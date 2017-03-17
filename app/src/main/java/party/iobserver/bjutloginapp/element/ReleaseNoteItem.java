package party.iobserver.bjutloginapp.element;

/**
 * Created by ZeroGo on 2017.3.15.
 */

public class ReleaseNoteItem {
    private String title;
    private String note;

    public ReleaseNoteItem() {
    }

    public ReleaseNoteItem(String title, String note) {
        this.title = title;
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
