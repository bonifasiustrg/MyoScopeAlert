package com.apicta.myoscopealert.audiopicker


import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clipScrollableContainer
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apicta.myoscopealert.audiopicker.PickerUtils.getAudio
import com.apicta.myoscopealert.audiopicker.PickerUtils.permissionState
import com.apicta.myoscopealert.audiopicker.PickerUtils.toDp
import com.apicta.myoscopealert.audiopicker.PickerUtils.toPx
import com.apicta.myoscopealert.ui.theme.primary
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilePickerDialog(
    config: PickerConfig,
    modes: List<PickerMode> = PickerUtils.allModes,
    onDismissDialog: () -> Unit,
    selectedFiles: (List<PickerFile>) -> Unit,
) {
    val context = LocalContext.current
    val contextWrapper = ContextWrapper(context)
//    val externalStorage: File = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)!!
    val audioDirPath = /*externalStorage*/Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)?.absolutePath



    val permissionsState = permissionState(config.enableCamera)
    if (!permissionsState.allPermissionsGranted) {
        LaunchedEffect(key1 = true, block = {
            permissionsState.launchMultiplePermissionRequest()
        })
    } else {

        val c = LocalContext.current
//        val images = remember { mutableStateListOf<PickerFile>() }.apply {
//            addAll(getImage(c, config.enableCamera))
//        }
        val audios = remember { mutableStateListOf<PickerFile>() }.apply {
            addAll(getAudio(c))
        }
//        val videos = remember { mutableStateListOf<PickerFile>() }.apply {
//            addAll(getVideo(c))
//        }

        val currentType = remember { mutableStateOf(config.currentType) }
        var searchText by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

        val files by remember {
            derivedStateOf {
                when (currentType.value) {
                    PickerType.Audio -> {
                        if (searchText.isEmpty())
                            audios
                        else
                            audios.filter { it.path.lowercase().contains(searchText.lowercase()) }
                    }
                }.distinctBy { it.path }
            }
        }

        val bottomSheetState = rememberModalBottomSheetState()

        val selectedFilesCount = remember { mutableIntStateOf(0) }


        BottomSheetDialog(
            pickerModes = modes.toImmutableList(),
            bottomSheetState = bottomSheetState,
            config = config,
            currentType = currentType.value,
            files = files.toImmutableList(),
            selectedCount = selectedFilesCount.value,
            itemTypeClick = { currentType.value = it },
            onDismissDialog = onDismissDialog,
            searchText = searchText,
            onSearchChange = {
                searchText = it
            },
            onChangeSelect = { pickerFile ->
                when (currentType.value) {

                    PickerType.Audio -> {
                        val index = audios.indexOfFirst { it.path == pickerFile.path }.takeIf { it >= 0 } ?: return@BottomSheetDialog
                        audios[index] = audios[index].copy(selected = !pickerFile.selected)
                    }
                }

                val total = (/*images +*/ audios /*+ videos*/).filter { it.selected }

                if (total.size > config.maxSelection) {
                    total.firstOrNull()?.let {
//                        images.indexOfFirst { it == total.first() }.takeIf { it > 0 }?.apply { images[this] = images[this].copy(selected = false) }
//                        videos.indexOfFirst { it == total.first() }.takeIf { it > 0 }?.apply { videos[this] = videos[this].copy(selected = false) }
                        audios.indexOfFirst { it == total.first() }.takeIf { it > 0 }?.apply { audios[this] = audios[this].copy(selected = false) }
                    }
                }

                if (total.size != selectedFilesCount.value)
                    selectedFilesCount.value = total.size
            },
            onDoneClick = {
                val s = (/*images.flatMap { listOf(it) } +*/ audios.flatMap { listOf(it) } /*+ videos.flatMap { listOf(it) }*/).filter { it.selected }.distinctBy { it.path }
                Log.e("done", "$s")

                // Mengambil direktori tujuan
                val destinationDirectory = File(audioDirPath)

                // Menyalin file yang dipilih ke direktori tujuan
                s.forEach { pickerFile ->
                    val sourceFile = File(pickerFile.path)
                    val destinationFile = File(destinationDirectory, sourceFile.name)

                    try {
                        sourceFile.copyTo(destinationFile, overwrite = true)
                        Log.e("Copy", "File ${pickerFile.path} berhasil disalin ke ${destinationFile.absolutePath}")
                    } catch (e: Exception) {
                        Log.e("Copy", "Gagal menyalin file ${pickerFile.path}: ${e.message}")
                    }
                }

                selectedFiles.invoke(s)
                onDismissDialog()
                coroutineScope.launch { bottomSheetState.hide() }
            },
            onCameraPhoto = {
            },
            onStoragePicker = { list ->
                selectedFiles.invoke(list.distinctBy { it.path })
                onDismissDialog()
                coroutineScope.launch { bottomSheetState.hide() }
            }
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialog(
    pickerModes: ImmutableList<PickerMode>,
    bottomSheetState: SheetState,
    config: PickerConfig,
    currentType: PickerType,
    files: ImmutableList<PickerFile>,
    selectedCount: Int,
    itemTypeClick: (PickerType) -> Unit,
    onChangeSelect: (PickerFile) -> Unit,
    searchText: String = "",
    onSearchChange: (String) -> Unit,
    onDismissDialog: () -> Unit,
    onDoneClick: () -> Unit,
    onCameraPhoto: (PickerFile) -> Unit,
    onStoragePicker: (List<PickerFile>) -> Unit,
    isLandscape: Boolean = LocalConfiguration.current.orientation != Configuration.ORIENTATION_PORTRAIT

) {
    val density = LocalDensity.current.density
    var modalHeight by remember { mutableIntStateOf(0) }
    var modalWidth by remember { mutableIntStateOf(0) }
    var footerHeight by remember { mutableIntStateOf(0) }

    val bottomPadding = ButtonDefaults.MinHeight.toPx()


    var horizontalArrangement by remember { mutableStateOf(Arrangement.SpaceEvenly) }
    LaunchedEffect(key1 = selectedCount, block = {
        horizontalArrangement = if (selectedCount > 0 && !isLandscape) Arrangement.spacedBy(16.dp) else Arrangement.SpaceEvenly
    })

    val doneAlpha = animateFloatAsState(targetValue = if (selectedCount > 0) 1f else 0f, label = "doneAlphaAnimation")



    ModalBottomSheet(
        modifier = Modifier
            .defaultMinSize(minHeight = modalHeight.toDp())
            .onGloballyPositioned {
                modalHeight = it.size.height
                modalWidth = it.size.width
            },
        sheetState = bottomSheetState,
        containerColor = primary/*config.containerColor*/,
        scrimColor = /*config.scrimColor*/Color.Transparent ?: Color.Gray,
        onDismissRequest = onDismissDialog
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {

            when (currentType) {

                PickerType.Audio -> {
                    if (files.isEmpty())
                        Text(modifier = Modifier.fillMaxWidth(), text = config.noItemMessage, style = config.noItemStyle)
                    else
                        AudioScreen(config, modalHeight, files, onChangeSelect, searchText, onSearchChange)
                }
            }

            /*Lazy*/Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(150.dp)
                    .offset {
                        IntOffset(
                            0,
                            (modalHeight - bottomSheetState.requireOffset() - footerHeight).toInt()
                        )
                    }
                    .fillMaxWidth()
                    .padding(top = 12.dp, end = 0.dp, start = 0.dp, bottom = 42.dp)
                    .onGloballyPositioned {
                        footerHeight = (((it.size.height * 1)) + bottomPadding + 0).toInt()
                    }
//                    .drawBehind {
//                        drawLine(
//                            color = Color.DarkGray,
//                            start = Offset(0f, 0f),
//                            end = Offset(size.width, 0f),
//                            strokeWidth = 5f
//                        )
//                    }
//                    .shadow(6.dp, RoundedCornerShape(1.dp), spotColor = Color.Gray)
                    .background(color = Color.Transparent/*config.containerColor*/, RoundedCornerShape(1.dp))
                    .padding(16.dp),
                horizontalArrangement = horizontalArrangement,
//                contentPadding = PaddingValues(16.dp)
            ) {

//                items(pickerModes.size, key = { pickerModes[it].title }) { index ->
//                    val item = pickerModes[index]
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.SpaceAround
//                    ) {
////                        Icon(
////                            painter = painterResource(id = item.icon),
////                            contentDescription = "${item.pickerType.name} icon",
////                            Modifier
////                                .size(item.iconSize)
////                                .padding(4.dp)
////                                .shadow(2.dp, item.shape, spotColor = Color.Gray)
////                                .background(item.shapeColor, item.shape)
////                                .then(
////                                    if (currentType == item.pickerType) Modifier
////                                        .padding(4.dp)
////                                        .border(1.5.dp, color = Color.White.copy(alpha = 0.5f), shape = CircleShape)
////                                    else Modifier
////                                )
////                                .padding(12.dp)
////                                .clickable { itemTypeClick(item.pickerType) },
////                            tint = item.iconTint,
////                        )
////
////                        Text(
////                            text = item.title,
////                            color = if (currentType == item.pickerType) item.selectedColor else item.itemTextStyle.color,
////                            textAlign = TextAlign.Center,
////                            style = item.itemTextStyle,
////                            modifier = Modifier.clickable { itemTypeClick(item.pickerType) }
////                        )
//                    }
//                }
            }


            Row(
                modifier = Modifier
                    .alpha(doneAlpha.value)
                    .offset {
                        IntOffset(
                            modalWidth - (68.dp * density).value.toInt(),
                            (modalHeight - bottomSheetState.requireOffset() - (1.1 * footerHeight)).toInt()
                        )
                    }
                    .clickable { onDoneClick() }
                    .padding(0.dp)) {

                Icon(
                    modifier =
                    Modifier
                        .size(config.doneIconSize)
                        .shadow(2.dp, CircleShape, spotColor = Color.Gray)
                        .background(config.doneIconBackground, CircleShape),
                    tint = config.doneIconTint,
                    imageVector = Icons.Default.UploadFile/*painterResource(id = config.doneIcon)*/,
                    contentDescription = "done icon"
                )

                if (selectedCount > 0)
                    Text(
                        text = "$selectedCount",
                        modifier = Modifier
                            .wrapContentSize(unbounded = true)
                            .border(1.dp, config.containerColor, shape = CircleShape)
                            .sizeIn(20.dp, 20.dp, 30.dp, 30.dp)
                            .shadow(2.dp, CircleShape, spotColor = Color.Gray)
                            .drawBehind {
                                drawCircle(
                                    color = config.doneBadgeBackgroundColor,
                                    radius = this.size.maxDimension
                                )
                            }
                            .scale(1f)/*
                            .align(Alignment.BottomEnd)*/,
                        style = config.doneBadgeStyle,
                    )
            }
        }
    }

    LaunchedEffect(key1 = isLandscape, block = {
        delay(500)
        bottomSheetState.expand()
    })


}

@Composable
fun AudioScreen(
    config: PickerConfig,
    modalHeight: Int,
    files: ImmutableList<PickerFile>,
    onChangeSelect: (PickerFile) -> Unit,
    searchText: String,
    onSearchChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = modalHeight.toDp())
            .fillMaxWidth()
    ) {
        TextField(
            value = searchText,
            onValueChange = {
                onSearchChange(it)
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = /*config.searchTextStyle.color*/Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
            placeholder = {
                Text(
                    config.searchTextHint,
                    modifier = Modifier.fillMaxWidth(),
                    style = config.searchTextHintStyle,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )


        LazyColumn(
            modifier = Modifier.clipScrollableContainer(Orientation.Vertical),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(
                top = 16.dp,
                end = 16.dp,
                start = 16.dp,
                bottom = 150.dp
            ),
        ) {
            items(files.size, key = { files[it].path }) { index ->
                MediaAudioItem(files[index]) {
                    onChangeSelect(files[index])
                }
            }
        }
    }

}