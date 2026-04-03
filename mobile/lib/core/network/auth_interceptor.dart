import 'package:dio/dio.dart';
import 'package:go_router/go_router.dart';
import '../storage/secure_storage.dart';

class AuthInterceptor extends Interceptor {
  AuthInterceptor(this._secureStorage, this._router);

  final SecureStorage _secureStorage;
  final GoRouter _router;

  @override
  Future<void> onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    final token = await _secureStorage.getToken();
    if (token != null) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  }

  @override
  Future<void> onError(
    DioException err,
    ErrorInterceptorHandler handler,
  ) async {
    if (err.response?.statusCode == 401) {
      await _secureStorage.deleteToken();
      _router.go('/login');
    }
    handler.next(err);
  }
}
