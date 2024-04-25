import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import io.quarkus.security.UnauthorizedException

fun UserAuthenticationService.getUser(): Person = getUserOrNull()
	?: throw UnauthorizedException("User not found!")
