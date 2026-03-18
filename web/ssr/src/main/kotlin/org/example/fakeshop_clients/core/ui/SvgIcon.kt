package org.example.fakeshop_clients.core.ui

import kotlinx.html.FlowContent
import kotlinx.html.span
import kotlinx.html.unsafe

fun FlowContent.svgIcon(
    pathData: String,
    cssClass: String,
    viewBox: String = "0 0 24 24"
) {
    span {
        unsafe {
            +"""<svg class="$cssClass" viewBox="$viewBox" xmlns="http://www.w3.org/2000/svg"><path d="$pathData"/></svg>"""
        }
    }
}
