package com.mauquoi.money.business.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Contains some errors class that can be thrown during runtime
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException : RuntimeException("Nobody could be found by that ID.")

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
class StatusAlreadyOccupiedException : RuntimeException("This user already has this role.")

@ResponseStatus(value = HttpStatus.CONFLICT)
class EmailAlreadyUsedException : RuntimeException("This email already has an account here.")

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class IncorrectCredentialsException : RuntimeException("The email or password are incorrect.")

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class IncorrectCodeException : RuntimeException("The email or verification code are incorrect.")