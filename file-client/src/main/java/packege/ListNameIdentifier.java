package packege;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EGS on 25.11.2015.
 */
public class ListNameIdentifier {
    private List<NameIdentifier> list;

    ListNameIdentifier(){
        list = new ArrayList<NameIdentifier>();
    }

    public List<NameIdentifier> getList() {
        return list;
    }

    public void setList(List<NameIdentifier> list) {
        this.list = list;
    }
}
