package tmpobjs;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chimaeraqm.simweather.R;

import java.util.List;

/**
 * Created by Administrator on 2017/11/28.
 */

public class FruitAdapter extends ArrayAdapter<Fruit> {

    private int resourceId;
    public FruitAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Fruit> fruits) {
        super(context, resource, fruits);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Fruit fruit = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            /**
             * this section is mainly used to optimize two points:
             * 1) use convertView to reuse view already loaded before;
             * 2) store widgets into viewholder, and store viewHolder into view itself,
             * actually store widgets in the view for easy access in further usage.
             */
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            viewHolder.fruitText = (TextView) view.findViewById(R.id.fruit_text);
            /**
             * store viewHolder into view
             */
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.fruitImage.setImageResource(fruit.getImageId());
        viewHolder.fruitText.setText(fruit.getName());
        return view;
    }

    class ViewHolder
    {
        ImageView fruitImage;
        TextView fruitText;
    }
}
