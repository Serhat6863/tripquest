sealed class ApiResponse<T> {
  const ApiResponse();
}

final class ApiSuccess<T> extends ApiResponse<T> {
  const ApiSuccess(this.data);

  final T data;
}

final class ApiError<T> extends ApiResponse<T> {
  const ApiError(this.message, this.statusCode);

  final String message;
  final int? statusCode;
}
