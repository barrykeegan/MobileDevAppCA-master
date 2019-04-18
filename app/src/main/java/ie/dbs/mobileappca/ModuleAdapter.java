package ie.dbs.mobileappca;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> {

    private List<Object> modules;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView module_name;
        public TextView course_name;
        public TextView lecturer;
        public TextView start;
        public TextView end;
        public View layout;
        public ImageView avatar;
        public ViewHolder(View v) {
            super(v);
            layout = v;
            module_name = (TextView) v.findViewById(R.id.module_name);
            course_name = (TextView) v.findViewById(R.id.course_name);
            lecturer = (TextView) v.findViewById(R.id.lecturer);
            start = (TextView) v.findViewById(R.id.start);
            end = (TextView) v.findViewById(R.id.end);
            avatar = (ImageView) v.findViewById(R.id.avatar);
        }
    }

    @Override
    public ModuleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View moduleView = inflater.inflate(R.layout.module, parent, false);
        ViewHolder module = new ViewHolder(moduleView);
        return module;
    }

    public ModuleAdapter(List<Object> dataset) {
        modules = dataset;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Map module = (HashMap) modules.get(position);
        holder.module_name.setText(module.get("Module_Code") + " - " + module.get("Module_Name"));
        holder.course_name.setText(module.get("Course") + " - " + module.get("Course_Intake"));
        holder.lecturer.setText(module.get("Lecturer").toString());
        holder.start.setText(module.get("From").toString());
        holder.end.setText(module.get("To").toString());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    @Override
    public int getItemCount() {
        return modules.size();


}
}
