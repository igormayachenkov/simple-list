package ru.igormayachenkov.list

import ru.igormayachenkov.list.data.DataItem

fun fillMockData(){
    // SHOPPING LIST
    Database.insertItem(
        DataItem(
            id = 13,
            parent_id = 0,
            DataItem.Type(hasChildren = true, isCheckable = false),
            DataItem.State(isChecked = false),
            name = "A shopping list example",
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1301,
            parent_id = 13,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = "bread",
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1301,
            parent_id = 13,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = "butter",
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1302,
            parent_id = 13,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = "fresh milk",
            description = "Check expiration date!"
        )
    )
    Database.insertItem(
        DataItem(
            id = 1303,
            parent_id = 13,
            DataItem.Type(hasChildren = false, isCheckable = false),
            DataItem.State(isChecked = false),
            name = "ice cream every day",
            description = "the checkbox is not required here, it can't be completed"
        )
    )
    Database.insertItem(
        DataItem(
            id = 1304,
            parent_id = 13,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = true),
            name = "avocado",
            description = "already bought, so at the end of the list"
        )
    )

    // NESTED LISTS
    Database.insertItem(
        DataItem(
            id = 14,
            parent_id = 0,
            DataItem.Type(hasChildren = true, isCheckable = false),
            DataItem.State(isChecked = false),
            name = "Nested lists example",
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1410,
            parent_id = 14,
            DataItem.Type(hasChildren = true, isCheckable = false),
            DataItem.State(isChecked = false),
            name = "Sublist A",
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1411,
            parent_id = 1410,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = "Sublist A. Item 1",
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1412,
            parent_id = 1410,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = "Sublist A. Item 2",
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1420,
            parent_id = 14,
            DataItem.Type(hasChildren = true, isCheckable = false),
            DataItem.State(isChecked = false),
            name = "Sublist B",
            description = null
        )
    )
    Database.insertItem(
        DataItem(
            id = 1430,
            parent_id = 14,
            DataItem.Type(hasChildren = false, isCheckable = true),
            DataItem.State(isChecked = false),
            name = "A simple checkable item also can be here",
            description = null
        )
    )
}