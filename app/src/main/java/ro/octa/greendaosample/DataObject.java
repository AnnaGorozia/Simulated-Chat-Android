package ro.octa.greendaosample;

import java.util.List;

public class DataObject {
    private List<Item> item; // This is for the inner array

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }
}
