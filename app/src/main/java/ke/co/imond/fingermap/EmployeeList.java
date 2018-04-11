package ke.co.imond.fingermap;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ke.co.imond.fingermap.Data.Employee;

/**
 * Developed by root on 11/6/17.
 */

public class EmployeeList extends ArrayAdapter<Employee> {
    private Activity context;
    private List<Employee> employeeList;

    public EmployeeList(Activity context, List<Employee> employeeList){
        super(context, R.layout.employees_view, employeeList);

        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
//        View listViewItem = inflater.inflate(R.layout.students_view, null, true);

//        TextView textViewName = (TextView)listViewItem.findViewById(R.id.textViewName);
//        TextView textViewdetail = (TextView)listViewItem.findViewById(R.id.textViewdetails);
//
        Employee employee = employeeList.get(position);
//
//        textViewName.setText(student.getFirstName());
//
////        student.getClassLevel().equals("wer");

        View listViewItem=convertView;
        ViewHolder holder = new ViewHolder();
        if(convertView==null){

            listViewItem = inflater.inflate(R.layout.list_row, null);


            holder.textViewName = (TextView)listViewItem.findViewById(R.id.textViewName); // city name
            holder.textViewDetails = (TextView)listViewItem.findViewById(R.id.textViewdetails); // weather
            holder.textViewAvatar =(ImageView)listViewItem.findViewById(R.id.list_image); // thumb image

            listViewItem.setTag(holder);
        }
        else{
            holder = (ViewHolder)listViewItem.getTag();
        }

// Setting all values in listview

        holder.textViewName.setText(employee.getFirst_name() + " " + employee.getLast_name());
        holder.textViewDetails.setText(employee.getPf_no() +", Designate:"+employee.getDesignation());

        //Setting an image
        String uri = "mipmap/admin";
        int imageResource = listViewItem.getContext().getApplicationContext().getResources().getIdentifier(
                uri, null, listViewItem.getContext().getApplicationContext().getPackageName());
        Drawable image = listViewItem.getContext().getResources().getDrawable(imageResource);
        holder.textViewAvatar.setImageDrawable(image);

        return listViewItem;
    }

    static class ViewHolder {
        TextView textViewName;
        TextView textViewDetails;
        ImageView textViewAvatar;
    }


}
