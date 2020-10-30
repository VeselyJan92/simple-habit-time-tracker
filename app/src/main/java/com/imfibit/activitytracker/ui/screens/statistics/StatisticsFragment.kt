package com.imfibit.activitytracker.ui.screens.statistics

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.viewModel
import androidx.fragment.app.Fragment
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.screens.activity_list.Goal
import com.thedeanda.lorem.LoremIpsum
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class StatisticsFragment : Fragment() {
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
                topBar = { TrackerTopAppBar("Statistika") },
                bodyContent = { ScreenBody() },
                backgroundColor = Colors.AppBackground,
            )
        }
    }

}

@Composable
private fun ScreenBody() = Column {
    val clock = AnimationClockAmbient.current

    val vm = viewModel<StatisticsViewModel>()

    val pager = remember(clock){
        PagerState(clock, 100, minPage = 0, maxPage = 100){
            vm.onSwipedCard(maxPage - currentPage)
        }
    }

    Navigation(vm, pager = pager)

    Pager(state = pager, modifier = Modifier.padding(bottom = 8.dp)) {

        ScrollableColumn(Modifier) {
            BlockTimeTracked(vm)
            BlockScores(vm)
            BlockCompleted(vm)
        }
    }
}

@Composable
private fun Navigation(vm: StatisticsViewModel, pager: PagerState){
    Surface(modifier = Modifier.padding(8.dp).background(Color.White), elevation = 2.dp) {
        Column{

            Row(Modifier.padding(horizontal = 8.dp).padding(top = 8.dp)) {

                TimeRange.values().forEach {timeRange ->
                    val color = if (vm.range == timeRange)
                        Colors.ChipGraySelected
                    else
                        Colors.ChipGray

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end =  8.dp)
                            .height(30.dp)
                            .background(color, RoundedCornerShape(50))
                            .clickable(onClick = {
                                vm.date = LocalDate.now()
                                vm.range = timeRange
                            }),

                        alignment = Alignment.Center
                    ) {
                        Text(text = stringResource(id = timeRange.label))
                    }
                }
                
                Spacer(modifier = Modifier.width(50.dp))

                val context = ContextAmbient.current

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(30.dp)
                        .background(Colors.ChipGray, RoundedCornerShape(50))
                        .clickable(
                            onClick = {
                                DatePickerDialog(context, 0,
                                    { _, i, i2, i3 -> vm.date =  LocalDate.of(i, i2, i3)},
                                    vm.date.year, vm.date.month.value, vm.date.dayOfMonth
                                ).show()
                            }
                        ),
                    alignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarToday)
                }
            }

            Divider(Modifier.padding(top = 8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val mod = Modifier.height(30.dp).background(Colors.ButtonGreen, RoundedCornerShape(50))

                Icon(Icons.Default.ArrowLeft.copy(defaultHeight = 30.dp, defaultWidth = 30.dp))

                Spacer(Modifier.weight(1f))

                val label = when(vm.range){
                    TimeRange.DAILY -> vm.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                    TimeRange.WEEKLY -> vm.range.getBoundaries(vm.date).run { "$first - $second" }
                    TimeRange.MONTHLY -> stringArrayResource(R.array.months)[vm.date.monthValue-1]
                }

                Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)

                Spacer(Modifier.weight(1f))

                Icon(Icons.Default.ArrowRight.copy(defaultHeight = 30.dp, defaultWidth = 30.dp))
            }
        }
    }
}

@Composable
private fun Header(title: String, icon: VectorAsset, last: @Composable (() -> Unit)? = null){
    Row(modifier = Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {

        Icon( icon, modifier = Modifier.padding(end = 16.dp))

        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.W600,
                fontSize = 20.sp
            ),
        )

        Spacer(modifier = Modifier.weight(1f))

        last?.invoke()
    }

}

@Composable
private fun BlockTimeTracked(scope: StatisticsViewModel) {
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {
        Column(Modifier.padding(8.dp)) {
            Header(title = "Časoběrné aktivity", icon = Icons.Default.Timer){
                Box(
                    modifier = Modifier.size(60.dp, 25.dp).background(Colors.ChipGray, RoundedCornerShape(50)),
                    alignment = Alignment.Center
                ){
                    Text("54:23",  style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ))
                }
            }




            repeat(4){
                Row(Modifier.padding( start = 8.dp, end= 8.dp)) {
                    Text(text = LoremIpsum.getInstance().getWords(1, 3), fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))


                    if( it ==3)
                        Goal(label = "11:00")

                    BaseMetricBlock(metric = "12:45", color = Colors.AppAccent, metricStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ))

                }

                if (it!= 4)
                    Divider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }


            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.today),
                    color = Colors.AppAccent,
                    width = 40.dp,
                    metricStyle = metricTextStyle.copy(fontWeight = FontWeight.Bold)
                )
                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.week),
                    color = Colors.AppAccent,
                    width = 40.dp
                )
                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.month),
                    color = Colors.ChipGray,
                    width = 40.dp,
                    metricStyle = metricTextStyle.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                )
                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.days30),
                    color = Colors.AppAccent,
                    width = 40.dp
                )

                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.days30),
                    color = Colors.AppAccent,
                    width = 40.dp
                )
            }
        }
    }

}



@Composable
private fun BlockScores(scope: StatisticsViewModel) {
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {
        Column(Modifier.padding(8.dp)) {

            Header(title = "Skore", Icons.Default.Score)


            repeat(3){
                Row(Modifier.padding( start = 8.dp, end= 8.dp)) {
                    Text(text = LoremIpsum.getInstance().getWords(1, 3), fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))


                    if( it ==2)
                        Goal(label = "20")

                    BaseMetricBlock(metric = "23", color = Colors.AppAccent, metricStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ))

                }

                if (it!= 4)
                    Divider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }

        }
    }
}


@Composable
private fun BlockCompleted(scope: StatisticsViewModel) {
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {
        Column(Modifier.padding(8.dp)) {

            Header(title = "Zvyky", Icons.Default.AssignmentTurnedIn)


            repeat(3){
                Row(Modifier.padding( start = 8.dp, end= 8.dp)) {
                    Text(text = LoremIpsum.getInstance().getWords(1, 3), fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))


                    BaseMetricBlock(metric = "3/7", color = Colors.AppAccent, metricStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ))

                }

                if (it!= 4)
                    Divider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }

        }
    }

}

