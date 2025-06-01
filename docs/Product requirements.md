Triples VPN Website Blocker - Android Native PRD
Product Requirements Document (PRD)
1. Product Overview
Product Name: Triples VPN Website Blocker
Platform: Android Native (Kotlin)
Target SDK: 34 (Android 14)
Minimum SDK: 26 (Android 8.0)
Primary User: Privacy-conscious users who want to block distracting websites

2. Product Vision
Triples is a clean, simple Android app that helps users block distracting websites by integrating with a local VPN service. The app provides an intuitive interface to add, remove, and toggle website blocking with minimal friction.

3. User Stories
As a user, I want to add websites to block so I can avoid distractions
As a user, I want to toggle website blocking on/off so I can control when sites are accessible
As a user, I want to remove websites from my block list when I no longer need to block them
As a user, I want to see website favicons so I can quickly identify sites in my list
As a user, I want a clean, native Android interface that feels like a premium app
4. Feature Requirements
4.1 Core Features
Website Management

Add websites via dialog
Display websites with favicons
Toggle website blocking state (locked/unlocked)
Remove websites from list
VPN Integration

Local VPN service to intercept and block web requests
Auto-start VPN when app launches
Notification when VPN is active
UI Components

Material Design 3 components
Custom lock/unlock state icons
Website favicon display
Empty state guidance
4.2 Technical Requirements
Architecture

MVVM architecture pattern
Repository pattern for data access
Kotlin Coroutines for asynchronous operations
Jetpack Compose for UI
Data Storage

Room database for website storage
DataStore for app preferences
In-memory cache for active blocking rules
VPN Implementation

VpnService implementation
Packet interception and filtering
DNS-based blocking mechanism
UI Framework

Jetpack Compose for modern UI
Material 3 design system
Custom theme with brand colors
5. UI/UX Specifications
5.1 App Structure
Main Screen

App bar with logo and title
FAB for adding websites
List of websites with toggle and delete actions
Empty state when no websites added
Add Website Dialog

Website URL input field
Cancel and Add buttons
Input validation
VPN Status

Status indicator in app bar
Persistent notification when active
5.2 Design Elements
Color Palette

Primary: 
#60E329 (Green from logo)
Secondary: 
#FFFFFF (White)
Background: 
#F9FAFB (Light gray)
Error: 
#EF4444 (Red for locked state)
Surface: 
#FFFFFF (White for cards)
Typography

Headings: Roboto Medium
Body: Roboto Regular
Buttons: Roboto Medium
Icons

Custom SVG icons for lock states
Material icons for standard actions
Website favicons from Google Favicon service
Components

Rounded corners (16dp)
Elevation for cards (2dp)
Consistent padding (16dp)
6. Technical Architecture
6.1 Project Structure
app/
├── src/
│   ├── main/
│   │   ├── java/com/triples/vpnblocker/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── WebsiteDao.kt
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── model/
│   │   │   │   │   ├── Website.kt
│   │   │   │   ├── repository/
│   │   │   │   │   ├── WebsiteRepository.kt
│   │   │   ├── di/
│   │   │   │   ├── AppModule.kt
│   │   │   ├── service/
│   │   │   │   ├── VpnService.kt
│   │   │   │   ├── PacketProcessor.kt
│   │   │   ├── ui/
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   ├── components/
│   │   │   │   │   ├── WebsiteItem.kt
│   │   │   │   │   ├── AddWebsiteDialog.kt
│   │   │   │   │   ├── EmptyState.kt
│   │   │   │   ├── screens/
│   │   │   │   │   ├── MainScreen.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   ├── util/
│   │   │   │   ├── UrlUtils.kt
│   │   │   │   ├── FaviconLoader.kt
│   │   │   ├── viewmodel/
│   │   │   │   ├── MainViewModel.kt
│   │   │   ├── TriplesApplication.kt
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   │   ├── app_logo.xml
│   │   │   │   ├── lock_blocked.xml
│   │   │   │   ├── lock_unlocked.xml
│   │   │   │   ├── delete_website.xml
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   ├── themes.xml
│   │   ├── AndroidManifest.xml
6.2 Dependencies
// Core Android
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'

// Compose
implementation 'androidx.activity:activity-compose:1.8.2'
implementation platform('androidx.compose:compose-bom:2023.10.01')
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.ui:ui-graphics'
implementation 'androidx.compose.ui:ui-tooling-preview'
implementation 'androidx.compose.material3:material3'

// Room
implementation 'androidx.room:room-runtime:2.6.1'
implementation 'androidx.room:room-ktx:2.6.1'
kapt 'androidx.room:room-compiler:2.6.1'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// Dependency Injection
implementation 'com.google.dagger:hilt-android:2.48'
kapt 'com.google.dagger:hilt-android-compiler:2.48'

// Image Loading
implementation 'io.coil-kt:coil-compose:2.5.0'

// DataStore
implementation 'androidx.datastore:datastore-preferences:1.0.0'

// Navigation
implementation 'androidx.navigation:navigation-compose:2.7.6'
Implementation Checklist for Cursor
Phase 1: Project Setup
Create new Android project with Kotlin and Jetpack Compose
Configure build.gradle with required dependencies
Set up project structure following MVVM architecture
Configure theme with brand colors (
#60E329 primary)
Add SVG icons to drawable resources
Set up Room database and entities
Phase 2: Data Layer
Create Website data model
Implement Room DAO for website operations
Create WebsiteRepository for data access
Set up DataStore for app preferences
Implement URL validation and cleaning utilities
Phase 3: UI Components
Design and implement app theme (colors, typography, shapes)
Create WebsiteItem composable for list items
Implement AddWebsiteDialog composable
Create EmptyState composable for when no websites exist
Implement favicon loading utility with Coil
Phase 4: Main Screen
Create MainViewModel with website management logic
Implement MainScreen composable with Scaffold
Add FloatingActionButton for adding websites
Implement LazyColumn for website list
Connect UI events to ViewModel actions
Phase 5: VPN Service
Create VpnService implementation
Implement packet interception logic
Create DNS resolver for website blocking
Set up VPN connection management
Add persistent notification for VPN status
Phase 6: Integration & Testing
Connect VPN service to website repository
Implement website blocking logic
Test website addition and removal
Test blocking and unblocking functionality
Verify UI states and transitions
Phase 7: Polishing
Add animations for state changes
Implement error handling and user feedback
Optimize performance for large website lists
Add app icon and splash screen
Prepare for Play Store submission
Key UI Components (Jetpack Compose)
1. MainScreen.kt
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val websites by viewModel.websites.collectAsState(initial = emptyList())
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "Triples Logo",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Triples",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Website Blocker",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddWebsiteDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Website")
            }
        }
    ) { paddingValues ->
        if (websites.isEmpty()) {
            EmptyState(
                modifier = Modifier.padding(paddingValues),
                onAddClick = { viewModel.showAddWebsiteDialog() }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(websites) { website ->
                    WebsiteItem(
                        website = website,
                        onToggleLock = { viewModel.toggleWebsiteLock(website.id) },
                        onDelete = { viewModel.deleteWebsite(website.id) }
                    )
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddWebsiteDialog(
            onDismiss = { viewModel.hideAddWebsiteDialog() },
            onAdd = { url -> viewModel.addWebsite(url) }
        )
    }
}
2. WebsiteItem.kt
@Composable
fun WebsiteItem(
    website: Website,
    onToggleLock: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Website favicon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://www.google.com/s2/favicons?domain=${website.url}&sz=32",
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    fallback = painterResource(id = R.drawable.ic_globe),
                    error = painterResource(id = R.drawable.ic_globe)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Website details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = website.url,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (website.isLocked) "Locked" else "Unlocked",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Lock/Unlock button
                IconButton(onClick = onToggleLock) {
                    Image(
                        painter = painterResource(
                            id = if (website.isLocked) R.drawable.lock_blocked 
                                 else R.drawable.lock_unlocked
                        ),
                        contentDescription = if (website.isLocked) "Locked" else "Unlocked",
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                // Delete button
                IconButton(onClick = onDelete) {
                    Image(
                        painter = painterResource(id = R.drawable.delete_website),
                        contentDescription = "Delete",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
3. AddWebsiteDialog.kt
@Composable
fun AddWebsiteDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var websiteUrl by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
Chat Input
Ask a follow up…
No file chosen
v0 may make mistakes. Please use with discretion.