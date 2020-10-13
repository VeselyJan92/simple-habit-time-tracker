
package com.imfibit.activitytracker.ui.screens.day_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.SectionHeader
import com.imfibit.activitytracker.ui.components.TrackedActivityRecord
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DayRecordsFragment : Fragment() {

    private val vm by viewModels<DayRecordsVM>{
        DayRecordsVMFactory(
            requireArguments().getLong("id"),
            LocalDate.parse(requireArguments().getString("date"), DateTimeFormatter.ISO_DATE)
        )
    }

    @ExperimentalFocus
    @ExperimentalFoundationApi
    @OptIn(ExperimentalLayout::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )  = ComposeView(requireContext()).apply {
        setContent {
            Scaffold(
                topBar = { TrackerTopAppBar(stringResource(id = R.string.screen_title_record_history)) },
                bodyContent = {
                    ScreenBody(vm)
                },
                backgroundColor = Colors.AppBackground
            )
        }
    }

}


@Composable
private fun ScreenBody(vm: DayRecordsVM) {
    Column {

        Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp)) {
                SectionHeader(vm.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)))

            }
        }

        val items = vm.records.observeAsState(listOf())

        if (items.value.isEmpty()){
            Box(Modifier.fillMaxWidth().fillMaxHeight(), alignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.no_records), style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }else{
            LazyColumnFor(
                items = items.value,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                TrackedActivityRecord(item = it )
            }
        }



    }

}


