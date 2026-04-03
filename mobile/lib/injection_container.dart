import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:get_it/get_it.dart';
import 'core/network/dio_client.dart';
import 'core/storage/secure_storage.dart';
import 'features/auth/data/repositories/auth_repository.dart';
import 'features/auth/presentation/bloc/auth_bloc.dart';

final sl = GetIt.instance;

Future<void> initDependencies() async {
  sl.registerLazySingleton<SecureStorage>(
    () => SecureStorage(const FlutterSecureStorage()),
  );

  sl.registerLazySingleton<DioClient>(() => DioClient());

  sl.registerLazySingleton<AuthRepository>(
    () => AuthRepository(sl<DioClient>().dio, sl<SecureStorage>()),
  );

  sl.registerFactory<AuthBloc>(() => AuthBloc(sl<AuthRepository>()));
}
