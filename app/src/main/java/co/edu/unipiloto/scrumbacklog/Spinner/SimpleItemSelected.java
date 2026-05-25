package co.edu.unipiloto.scrumbacklog.Spinner;
import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelected implements AdapterView.OnItemSelectedListener {

    private final Runnable callback;

    public SimpleItemSelected(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (callback != null) {
            callback.run();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No hacer nada
    }
}