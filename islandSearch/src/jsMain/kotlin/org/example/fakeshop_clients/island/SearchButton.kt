package org.example.fakeshop_clients.island

import react.FC
import react.Props
import react.dom.html.ReactHTML.button

val SearchButton = FC<Props> {
    button {
        onClick = {
            println("[SearchButton] Button clicked!")
            console.log("[SearchButton] Button clicked!")
        }
        +"Search"
    }
}