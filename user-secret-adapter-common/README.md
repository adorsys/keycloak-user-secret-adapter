# Purpose

## User Secret Model

Resource Servers need to encrypt some user data. For individual encryption, each user must have different encryption-secrets.

When the user loggs in with the identity provider, the identity provider retrieves the user secret for the defined audience an add them to the access token. In order to perform this task, we define the following model:

### User Password

A user always logs in with a password. The user password can be used to protect access to user secrets. In this case data will be lost when the user forgets his password.

### User Main Secret

The user main secret is a piece of information we use to encrypt other user secrets. On successful login, we retrieve the user-main-secret and hold it in the user session.

### User Resource Secret

The user resource secret is a resource specific secret. It is stored encrypted with the user main secret in the user attributes. 