package ru.igormayachenkov.list

import ru.igormayachenkov.list.data.DataItem

fun fillMockData(){
    // TO-DO LIST
    Database.insertItem(
        DataItem(
            id = 13,
            parent_id = 0,
            DataItem.Type(hasChildren = true, isCheckable = false),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_todo_list),
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1301,
            parent_id = 13,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_todo_1),
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1302,
            parent_id = 13,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_todo_2),
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1303,
            parent_id = 13,
            DataItem.Type(hasChildren = false, isCheckable = false),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_todo_3),
            description = app.getString(R.string.mock_todo_3_descr),
        )
    )


    // SHOPPING LIST (NESTED LIST)
    Database.insertItem(
        DataItem(
            id = 14,
            parent_id = 0,
            DataItem.Type(hasChildren = true, isCheckable = false),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_shopping_list),
            description = null
        )
    )
    // Sublist 1
    Database.insertItem(
        DataItem(
            id = 1410,
            parent_id = 14,
            DataItem.Type(hasChildren = true, isCheckable = false),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_shopping_s1),
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1411,
            parent_id = 1410,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_shopping_s1_1),
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1412,
            parent_id = 1410,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_shopping_s1_2),
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1413,
            parent_id = 1410,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = true),
            name = app.getString(R.string.mock_shopping_s1_3),
            description = app.getString(R.string.mock_shopping_s1_3_descr)
        )
    )
    // Sublist 2
    Database.insertItem(
        DataItem(
            id = 1420,
            parent_id = 14,
            DataItem.Type(hasChildren = true, isCheckable = false),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_shopping_s2),
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1421,
            parent_id = 1420,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_shopping_s2_1),
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1422,
            parent_id = 1420,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_shopping_s2_2),
            description = null
        )
    )
    // Root Item
    Database.insertItem(
        DataItem(
            id = 1430,
            parent_id = 14,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = app.getString(R.string.mock_shopping_3),
            description = app.getString(R.string.mock_shopping_3_descr)
        )
    )
}