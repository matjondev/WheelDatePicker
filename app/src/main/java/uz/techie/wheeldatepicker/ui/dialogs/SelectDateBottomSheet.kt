package uz.techie.wheeldatepicker.ui.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDateBottomSheet(
    initLocalDate: LocalDate,
    onDismiss: () -> Unit,
    onSelectDate: (LocalDate) -> Unit
) {
    var selectedDay by remember { mutableStateOf(initLocalDate.dayOfMonth) }
    var selectedMonth by remember { mutableStateOf(initLocalDate.month) }
    var selectedYear by remember { mutableStateOf(initLocalDate.year) }

    LaunchedEffect(selectedDay,selectedMonth,selectedYear) {
        onSelectDate(
            LocalDate(
                year = selectedYear,
                month = selectedMonth,
                dayOfMonth = selectedDay
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.White,
        contentColor = Color.Black,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            InfiniteCircularList(
                items = DateWrapper.getDays(selectedMonth, selectedYear),
                initialItem = selectedDay,
                onItemSelected = { _, item -> selectedDay = item },
                modifier = Modifier.widthIn(min = 30.dp),
            )
            Spacer(modifier = Modifier.width(32.dp))
            InfiniteCircularList(
                items = DateWrapper.getMonth(),
                initialItem = selectedMonth,
                onItemSelected = { _, item -> selectedMonth = item },
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(32.dp))
            InfiniteCircularList(
                items = DateWrapper.getYears(),
                initialItem = selectedYear,
                onItemSelected = { _, item -> selectedYear = item },
                modifier = Modifier,
            )
        }
    }
}

object DateWrapper {

    fun getYears() =
        (1970..2024).toList()

    fun getMonth() = Month.entries.toList()

    fun getDays(month: Month, year: Int): List<Int> {
        val month = Month.entries.indexOf(month) + 1
        val lastDayOfMonth = if (month != 2) {
            31 - (month - 1) % 7 % 2
        } else {
            if (year and 3 == 0 && (year % 25 != 0 || year and 15 == 0)) {
                29
            } else {
                28
            }
        }
        return (1..lastDayOfMonth).toList()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun <reified T> InfiniteCircularList(
    itemHeight: Dp = 30.dp,
    numberOfDisplayedItems: Int = 5,
    items: List<T>,
    initialItem: T,
    textStyle: TextStyle = TextStyle(
        fontSize = 20.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium
    ),
    textColor: Color = LocalContentColor.current.copy(alpha = 0.3f),
    selectedTextColor: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
    crossinline itemToString: (T) -> String = { it.toString() },
    crossinline onItemSelected: (index: Int, item: T) -> Unit = { _, _ -> }
) {
    val containerHeight = itemHeight * numberOfDisplayedItems
    val density = LocalDensity.current
    val scrollState = rememberLazyListState(items.indexOf(initialItem))

    var itemsState by remember {
        mutableStateOf(items)
    }
    LazyColumn(
        modifier = Modifier
            .then(modifier)
            .height(containerHeight),
        state = scrollState,
        flingBehavior = rememberSnapFlingBehavior(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = itemHeight * 2)
    ) {
        itemsIndexed(items, key = { i, item -> item.toString() }) { i, item ->
            val item = itemsState[i % itemsState.size]
            var rotationX by remember { mutableStateOf(0f) }
            var contentColor by remember { mutableStateOf(textColor) }
            var scale by remember { mutableStateOf(1f) }
            var alphaRadian by remember { mutableStateOf(0f) }
            val parentHeightPx = with(density) { containerHeight.toPx() }

            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .onGloballyPositioned { coordinates ->
                        val r = parentHeightPx * 0.6f
                        val posY =
                            parentHeightPx / 2 - (coordinates.positionInParent().y + coordinates.size.height / 2)

                        alphaRadian = asin(posY / r)
                        val alphaDegree = alphaRadian * (180 / PI).toFloat()

                        scale = 0.3f + 0.7f * cos(alphaRadian)
                        rotationX = alphaDegree

                        val isItemSelected = alphaDegree in -10f..10f
                        contentColor = if (isItemSelected) selectedTextColor else textColor

                        if (isItemSelected) {
                            onItemSelected(i, item)
                        }
                    }
                    .graphicsLayer {
                        this.rotationX = rotationX
                        this.scaleX = scale
                        this.scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = itemToString(item),
                    style = textStyle,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}