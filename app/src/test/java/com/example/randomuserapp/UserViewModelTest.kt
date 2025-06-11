import com.example.randomuserapp.user.UserViewModel
import com.example.randomuserapp.data.UserRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: UserRepository
    private lateinit var viewModel: UserViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = UserViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAddUsersCallsRepository() = runTest {
        viewModel.addUsers()
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.addUsers() }
    }

    @Test
    fun testClearUsersCallsRepository() = runTest {
        viewModel.clearUsers()
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.clearUsers() }
    }

    @Test
    fun testRefreshUsersCallsRepository() = runTest {
        viewModel.refreshUsers()
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.refreshUsers() }
    }
}