import 'package:dio/dio.dart';
import '../../../../core/network/api_response.dart';
import '../../../../core/network/base_api_service.dart';
import '../../../../core/storage/secure_storage.dart';
import '../models/auth_token.dart';

class AuthRepository extends BaseApiService {
  AuthRepository(Dio dio, this._storage) : super(dio);

  final SecureStorage _storage;

  Future<ApiResponse<AuthToken>> login(
    String email,
    String password,
  ) async {
    final result = await post<AuthToken>(
      '/api/auth/login',
      data: {'email': email, 'password': password},
      fromJson: (json) => AuthToken.fromJson(json as Map<String, dynamic>),
    );
    if (result is ApiSuccess<AuthToken>) {
      await _storage.saveToken(result.data.token);
    }
    return result;
  }

  Future<ApiResponse<AuthToken>> register(
    String username,
    String email,
    String password,
  ) async {
    final result = await post<AuthToken>(
      '/api/auth/register',
      data: {'username': username, 'email': email, 'password': password},
      fromJson: (json) => AuthToken.fromJson(json as Map<String, dynamic>),
    );
    if (result is ApiSuccess<AuthToken>) {
      await _storage.saveToken(result.data.token);
    }
    return result;
  }

  Future<void> logout() => _storage.deleteToken();

  Future<bool> isLoggedIn() => _storage.hasToken();
}
