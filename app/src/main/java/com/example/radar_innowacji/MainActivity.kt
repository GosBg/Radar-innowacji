
package com.example.radar_innowacji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch

private val Forest = Color(0xFF187C68)
private val ForestDark = Color(0xFF233E34)
private val Ink = Color(0xFF1C3034)
private val Muted = Color(0xFF65776E)
private val Page = Color(0xFFF8FAF9)
private val Line = Color(0xFFE1E8E5)

private data class Idea(
    val title: String,
    val category: String,
    val description: String,
    val author: String,
    val initials: String,
    val score: String,
    val tint: Color,
    val symbol: String,
    val stage: String
)

private val sampleIdeas = listOf(
    Idea("Sąsiedzki ogród wymiany", "Społeczeństwo", "Miejsce, w którym mieszkańcy wymieniają się nasionami, narzędziami i plonami.", "Maja Nowak", "MN", "4.9", Color(0xFFDCEBDD), "🌱", "W rozwoju"),
    Idea("Chwila dla zdrowia", "Zdrowie", "Proste codzienne przypomnienia pomagające budować zdrowsze nawyki.", "Aleksandra Kowalska", "AK", "4.8", Color(0xFFE2E9F0), "✦", "Pomysł"),
    Idea("Lokalna wymiana umiejętności", "Edukacja", "Wymieniaj się praktycznymi umiejętnościami z osobami z okolicy — od nauki języków po drobne naprawy.", "Szymon Wiśniewski", "SW", "4.7", Color(0xFFECE6DB), "✎", "W realizacji"),
    Idea("Mapa jakości wody", "Technologia", "Ułatw społecznościom dostęp do informacji o jakości lokalnych zasobów wodnych.", "Natalia Kamińska", "NK", "4.9", Color(0xFFDCECEE), "≈", "Badania"),
    Idea("Dostępne miasto", "Smart city", "Tworzona przez mieszkańców mapa tras bez barier i dostępnych miejsc.", "Jan Lewandowski", "JL", "4.6", Color(0xFFE7E2EE), "⌖", "Prototyp"),
    Idea("Sąsiedzki stół", "Społeczeństwo", "Łącz ludzi podczas wspólnych posiłków i otwartych, sąsiedzkich rozmów.", "Roksana Pawlak", "RP", "4.8", Color(0xFFF0E3DF), "♡", "W rozwoju")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { IdeaCatalogApp() }
    }
}

@Composable
fun IdeaCatalogApp() {
    var selectedTab by remember { mutableStateOf("Odkrywaj") }
    var selectedCategory by remember { mutableStateOf("Wszystkie") }
    var search by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf("Najnowsze") }
    var saved by remember { mutableStateOf(setOf<String>()) }
    var selectedIdea by remember { mutableStateOf<Idea?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var showSubmit by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val categories = listOf(
        "Wszystkie", "Zielona energia", "Zdrowie",
        "Edukacja", "Smart city", "Technologia", "Społeczeństwo"
    )

    val filtered = sampleIdeas.filter {
        (selectedCategory == "Wszystkie" ||
                it.category.equals(selectedCategory, true)) &&
                (search.isBlank() ||
                        it.title.contains(search, true) ||
                        it.description.contains(search, true))
    }.let {
        if (sort == "Najwyżej oceniane")
            it.sortedByDescending { idea -> idea.score.toDoubleOrNull() ?: 0.0 }
        else it
    }

    Scaffold(
        containerColor = Page,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                listOf("Odkrywaj", "Ulubione", "Wiadomości", "Profil").forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                when (tab) {
                                    "Odkrywaj" -> Icons.Default.Explore
                                    "Ulubione" -> Icons.Default.FavoriteBorder
                                    "Wiadomości" -> Icons.Default.ChatBubbleOutline
                                    else -> Icons.Default.PersonOutline
                                },
                                contentDescription = tab
                            )
                        },
                        label = { Text(tab, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Forest,
                            indicatorColor = Color(0xFFE6F1EC),
                            selectedTextColor = Forest
                        )
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                DemoStrip()
                TopHeader(
                    selectedTab,
                    onTab = { selectedTab = it },
                    onSubmit = { showSubmit = true }
                )
            }

            if (selectedTab == "Odkrywaj") {
                item {
                    HeroSection(
                        onExplore = {
                            selectedCategory = "Wszystkie"
                            search = ""

                            scope.launch {
                                listState.animateScrollToItem(4)
                            }
                        },
                        onSubmit = { showSubmit = true }
                    )
                }
                item { TrustStrip() }

                item {
                    Column(
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 28.dp)
                    ) {
                        Text(
                            "ODKRYJ TO, CO NADCHODZI",
                            color = Forest,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.7.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Na radarze innowacji",
                                color = Ink,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${filtered.size}",
                                color = Muted,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .background(Color(0xFFEAF1ED), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Znajdź pomysł, któremu pomożesz zrobić kolejny krok.",
                            color = Muted,
                            fontSize = 13.sp
                        )
                    }
                }

                item {
                    SearchAndFilters(
                        search = search,
                        onSearch = { search = it },
                        sort = sort,
                        onSort = { sort = it },
                        onToggleFilters = { showFilters = !showFilters }
                    )
                }

                item {
                    Row(
                        Modifier.fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            CategoryChip(
                                category,
                                selectedCategory == category
                            ) { selectedCategory = category }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                }

                if (showFilters) {
                    item {
                        FilterPanel(
                            onReset = {
                                selectedCategory = "Wszystkie"
                                sort = "Najnowsze"
                            }
                        )
                    }
                }

                item {
                    Row(
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${filtered.size} POMYSŁÓW DO ODKRYCIA",
                            color = Muted,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "Zweryfikowane przez moderatora •",
                            color = Forest,
                            fontSize = 10.sp
                        )
                    }
                }

                if (filtered.isEmpty()) {
                    item {
                        EmptyState {
                            search = ""
                            selectedCategory = "Wszystkie"
                        }
                    }
                } else {
                    items(filtered) { idea ->
                        IdeaCard(
                            idea = idea,
                            isSaved = saved.contains(idea.title),
                            onSave = {
                                saved = if (saved.contains(idea.title))
                                    saved - idea.title
                                else saved + idea.title
                            },
                            onClick = { selectedIdea = idea },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
            } else {
                item {
                    SecondaryPage(
                        title = when (selectedTab) {
                            "Ulubione" -> "Twoje ulubione pomysły"
                            "Wiadomości" -> "Twoja przestrzeń rozmowy"
                            else -> "Twój profil"
                        },
                        subtitle = when (selectedTab) {
                            "Ulubione" -> "Wracaj do rozwiązań, w których widzisz potencjał."
                            "Wiadomości" -> "Rozmawiaj z autorami pomysłów."
                            else -> "Zarządzaj swoim profilem."
                        },
                        count = if (selectedTab == "Ulubione") saved.size else 0
                    )
                }

                if (selectedTab == "Ulubione") {
                    val savedIdeas = sampleIdeas.filter { saved.contains(it.title) }

                    if (savedIdeas.isEmpty()) {
                        item { EmptyState { selectedTab = "Odkrywaj" } }
                    } else {
                        items(savedIdeas) { idea ->
                            IdeaCard(
                                idea, true,
                                { saved = saved - idea.title },
                                { selectedIdea = idea },
                                Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                    }
                } else if (selectedTab == "Wiadomości") {
                    item {
                        EmptyState { selectedTab = "Odkrywaj" }
                    }
                } else {
                    item {
                        Text(
                            "Profil użytkownika będzie dostępny w kolejnej wersji.",
                            color = Muted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }

            item { Footer() }
        }
    }

    selectedIdea?.let { idea ->
        IdeaDetailDialog(
            idea = idea,
            isSaved = saved.contains(idea.title),
            onSave = {
                saved = if (saved.contains(idea.title))
                    saved - idea.title
                else saved + idea.title
            },
            onDismiss = { selectedIdea = null }
        )
    }

    if (showSubmit) {
        SubmitIdeaDialog(onDismiss = { showSubmit = false })
    }
}

@Composable
private fun DemoStrip() {
    Box(
        Modifier.fillMaxWidth()
            .background(Color(0xFFE6F1EC))
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "HACKNATION 2026  •  PROTOTYP INTERAKTYWNY  •  DANE PRZYKŁADOWE",
            color = Color(0xFF326858),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
        )
    }
}

@Composable
private fun TopHeader(active: String, onTab: (String) -> Unit, onSubmit: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Color.White)
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                Modifier.size(38.dp).clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFBED5CB), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Eco, null, tint = Forest, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(9.dp))
            Column {
                Text("radar", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 21.sp)
                Text("INNOWACJI", color = Muted, fontSize = 7.sp, letterSpacing = 1.5.sp)
            }
        }
        IconButton(onClick = onSubmit, modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.AddCircleOutline, "Zgłoś pomysł", tint = Forest)
        }
    }

    Row(
        Modifier.fillMaxWidth().background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        listOf("Odkrywaj", "Ulubione", "Wiadomości", "Profil").forEach { tab ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onTab(tab) }.padding(vertical = 9.dp)
            ) {
                Text(
                    tab,
                    color = if (active == tab) Forest else Muted,
                    fontSize = 12.sp,
                    fontWeight = if (active == tab) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(Modifier.height(7.dp))
                Box(
                    Modifier.width(42.dp).height(2.dp)
                        .background(if (active == tab) Forest else Color.Transparent)
                )
            }
        }
    }
    HorizontalDivider(color = Line)
}

@Composable
private fun HeroSection(onExplore: () -> Unit, onSubmit: () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFFDDEEE5), Color(0xFFF2F7F3), Page)
                )
            )
            .padding(horizontal = 24.dp, vertical = 30.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(Forest))
            Spacer(Modifier.width(8.dp))
            Text(
                "PRZESTRZEŃ DLA WIELKICH POMYSŁÓW",
                color = Color(0xFF4A8070),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )
        }
        Spacer(Modifier.height(18.dp))
        Text("Mały pomysł.", color = Ink, fontSize = 36.sp, lineHeight = 42.sp, fontWeight = FontWeight.ExtraBold)
        Text("Wielkie możliwości.", color = Forest, fontSize = 36.sp, lineHeight = 42.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(14.dp))
        Text(
            "Odkrywaj innowacje, które zasługują na więcej. Łącz potencjał twórców z siłą partnerów.",
            color = Muted,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onExplore,
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Odkrywaj pomysły", fontSize = 12.sp)
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
            TextButton(onClick = onSubmit) {
                Text("Podziel się pomysłem", color = Ink, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(25.dp))
        RadarIllustration()
    }
}

@Composable
private fun RadarIllustration() {
    Box(Modifier.fillMaxWidth().height(205.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.size(190.dp).border(1.dp, Color(0x5582AD97), CircleShape))
        Box(Modifier.size(140.dp).border(1.dp, Color(0x5582AD97), CircleShape))
        Box(
            Modifier.size(82.dp).clip(CircleShape)
                .background(Forest).border(7.dp, Color(0xFFD1E8DB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Eco, null, tint = Color(0xFFE5F4E9), modifier = Modifier.size(32.dp))
        }
        SignalCard("Nowe połączenia", "Ludzie, którzy działają razem", Modifier.align(Alignment.TopStart))
        SignalCard("Pomysły nabierają mocy", "Wspólna zmiana", Modifier.align(Alignment.BottomEnd))
        Text(
            "POMYSŁY • WSPÓŁPRACA • MOŻLIWOŚCI",
            color = Color(0xFF85A18E),
            fontSize = 7.sp,
            letterSpacing = 1.2.sp,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SignalCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Row(
        modifier.clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = .96f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, null, tint = Forest, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(7.dp))
        Column {
            Text(title, color = Color(0xFF2D5F4C), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color(0xFF8B9B90), fontSize = 7.sp)
        }
    }
}

@Composable
private fun TrustStrip() {
    Row(
        Modifier.fillMaxWidth().background(Color.White)
            .border(1.dp, Line)
            .padding(horizontal = 17.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("✳  Ludzie są najważniejsi", "✓  Dobre pomysły", "♡  Wspólny rozwój").forEach {
            Text(it, color = Color(0xFF2E6854), fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SearchAndFilters(
    search: String,
    onSearch: (String) -> Unit,
    sort: String,
    onSort: (String) -> Unit,
    onToggleFilters: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            Modifier.weight(1f).clip(RoundedCornerShape(9.dp))
                .background(Color.White).border(1.dp, Line, RoundedCornerShape(9.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = Muted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = search,
                onValueChange = onSearch,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = Ink),
                decorationBox = { inner ->
                    if (search.isEmpty()) Text("Szukaj pomysłów, technologii, twórców…", color = Color(0xFF9AA79F), fontSize = 12.sp)
                    inner()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        IconButton(
            onClick = onToggleFilters,
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(9.dp))
                .background(Color.White).border(1.dp, Line, RoundedCornerShape(9.dp))
        ) {
            Icon(Icons.Default.Tune, "Filtry", tint = Forest)
        }
    }

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Sortuj:", color = Muted, fontSize = 11.sp)
        Spacer(Modifier.width(8.dp))
        var expanded by remember { mutableStateOf(false) }
        Box {
            TextButton(onClick = { expanded = true }, contentPadding = PaddingValues(0.dp)) {
                Text(sort, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                Icon(Icons.Default.ExpandMore, null, tint = Muted)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("Najnowsze", "Najwyżej oceniane").forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = { onSort(it); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.clip(RoundedCornerShape(7.dp))
            .background(if (selected) ForestDark else Color.White)
            .border(1.dp, if (selected) ForestDark else Line, RoundedCornerShape(7.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 9.dp)
    ) {
        Text(
            label,
            color = if (selected) Color.White else Muted,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun FilterPanel(onReset: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(11.dp)).background(Color.White)
            .border(1.dp, Line, RoundedCornerShape(11.dp)).padding(17.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Dopasuj wyniki", color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            TextButton(onClick = onReset, contentPadding = PaddingValues(0.dp)) {
                Text("Wyczyść", color = Forest, fontSize = 11.sp)
            }
        }
        Text(
            "Wybierz obszar powyżej lub posortuj pomysły według oceny społeczności.",
            color = Muted,
            fontSize = 11.sp,
            lineHeight = 17.sp
        )
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun IdeaCard(
    idea: Idea,
    isSaved: Boolean,
    onSave: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(Color.White).border(1.dp, Line, RoundedCornerShape(12.dp))
    ) {
        Box(
            Modifier.fillMaxWidth().height(160.dp).background(idea.tint)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.size(118.dp).border(1.dp, Color.White.copy(alpha = .55f), CircleShape))
            Box(
                Modifier.size(82.dp).clip(RoundedCornerShape(23.dp))
                    .background(Brush.linearGradient(listOf(Color.White.copy(alpha = .8f), Color.White.copy(alpha = .25f)))),
                contentAlignment = Alignment.Center
            ) {
                Text(idea.symbol, fontSize = 36.sp, color = Forest)
            }
            Text(
                idea.stage,
                color = Color(0xFF4F6959),
                fontSize = 9.sp,
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                    .clip(RoundedCornerShape(5.dp)).background(Color.White.copy(alpha = .88f))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            )
            IconButton(
                onClick = onSave,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    .size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = .95f))
            ) {
                Icon(
                    if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    "Dodaj do ulubionych",
                    tint = if (isSaved) Color(0xFFD8516A) else Muted,
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        Column(Modifier.padding(horizontal = 17.dp, vertical = 15.dp)) {
            Text(idea.category, color = Color(0xFF638975), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(7.dp))
            Text(idea.title, color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(7.dp))
            Text(idea.description, color = Muted, fontSize = 12.sp, lineHeight = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(13.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(30.dp).clip(CircleShape).background(Color(0xFFEEF2EE)), contentAlignment = Alignment.Center) {
                    Text(idea.initials, color = Color(0xFF6B8877), fontSize = 9.sp)
                }
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(idea.author, color = Ink, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    Text("Członek społeczności", color = Color(0xFF8E9A92), fontSize = 9.sp)
                }
                Icon(Icons.Default.Star, null, tint = Color(0xFFD6AD58), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(3.dp))
                Text(idea.score, color = Ink, fontSize = 10.sp)
            }
            HorizontalDivider(Modifier.padding(top = 13.dp, bottom = 11.dp), color = Color(0xFFEDF0ED))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pomysł społeczności", color = Muted, fontSize = 10.sp)
                TextButton(onClick = onClick, contentPadding = PaddingValues(0.dp)) {
                    Text("Zobacz pomysł", color = Forest, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.ArrowForward, null, tint = Forest, modifier = Modifier.size(13.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyState(onClear: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.SearchOff, null, tint = Color(0xFF789082), modifier = Modifier.size(36.dp))
        Spacer(Modifier.height(12.dp))
        Text("Nie znaleziono pomysłów", color = Ink, fontWeight = FontWeight.Bold)
        Text("Zmień wyszukiwanie lub wyczyść filtry.", color = Muted, fontSize = 12.sp)
        TextButton(onClick = onClear) { Text("Wyczyść filtry", color = Forest) }
    }
}

@Composable
private fun SecondaryPage(title: String, subtitle: String, count: Int) {
    Column(Modifier.fillMaxWidth().padding(24.dp)) {
        Text("TWOJA PRZESTRZEŃ", color = Forest, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
        Spacer(Modifier.height(8.dp))
        Text(title, color = Ink, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        Text(subtitle, color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(18.dp))
        Text(
            "Zapisane pomysły: $count",
            color = Forest,
            fontSize = 11.sp,
            modifier = Modifier.background(Color(0xFFEAF1ED), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun Footer() {
    Column(Modifier.fillMaxWidth().background(Color.White).padding(22.dp)) {
        HorizontalDivider(color = Line)
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Eco, null, tint = Forest)
            Spacer(Modifier.width(7.dp))
            Text("radar innowacji", color = ForestDark, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(7.dp))
        Text("Pomysły z potencjałem. Ludzie z wizją.", color = Muted, fontSize = 10.sp)
        Spacer(Modifier.height(12.dp))
        Text("PROTOTYP FRONTENDU  •  DANE PRZYKŁADOWE", color = Color(0xFF9AA79F), fontSize = 8.sp, letterSpacing = 1.sp)
    }
}

@Composable
private fun IdeaDetailDialog(
    idea: Idea,
    isSaved: Boolean,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(idea.title, color = Ink, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(idea.category, color = Forest, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                Text(idea.description, color = Muted, fontSize = 13.sp, lineHeight = 20.sp)
                Spacer(Modifier.height(12.dp))
                Text("Autor: ${idea.author}", color = Ink, fontSize = 12.sp)
                Text("Ocena społeczności  ★ ${idea.score}", color = Muted, fontSize = 11.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(if (isSaved) "Usuń z ulubionych" else "Dodaj do ulubionych", color = Forest)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Zamknij", color = Muted) }
        }
    )
}

@Composable
private fun SubmitIdeaDialog(onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Zgłoś pomysł", color = Ink, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nazwa innowacji") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Problem i proponowane rozwiązanie") },
                    minLines = 3
                )
                Text(
                    "Prototyp demonstracyjny — formularz nie wysyła danych na serwer.",
                    color = Muted,
                    fontSize = 10.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, enabled = title.isNotBlank()) {
                Text("Gotowe", color = Forest)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj", color = Muted) }
        }
    )
}
