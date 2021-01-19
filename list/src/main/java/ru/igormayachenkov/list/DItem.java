package ru.igormayachenkov.list;

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: Item

public class DItem {
    public static final int     ITEM_STATE_CHECKED  = 0b0001;

    long        id;
    int         syncState;
    int         state;
    String      name;
    String      description;

    void changeState(){
        // Change
        if(state==ITEM_STATE_CHECKED)
            state = 0;
        else
            state = ITEM_STATE_CHECKED;
        // Save
        Data.instance().database.updateItemState(id,state);
    }
}
