package com.fshou.ceritain

import com.fshou.ceritain.data.remote.response.Story

object DataDummy {

    fun generateStories() =
        List(100) {
                Story(
                    id = "$it",
                    name = "name $it",
                    description = "Lorem ipsum",
                    photoUrl = "photo $it",
                    createdAt = "createdAt $it",
                )
            }

}