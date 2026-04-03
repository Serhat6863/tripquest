import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'features/auth/presentation/bloc/auth_bloc.dart';
import 'features/auth/presentation/bloc/auth_event.dart';
import 'features/auth/presentation/bloc/auth_state.dart';
import 'features/auth/presentation/pages/login_page.dart';
import 'features/auth/presentation/pages/register_page.dart';
import 'features/globe/presentation/pages/globe_page.dart';
import 'features/splash/pages/splash_page.dart';
import 'injection_container.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await initDependencies();
  runApp(const TripQuestApp());
}

class TripQuestApp extends StatefulWidget {
  const TripQuestApp({super.key});

  @override
  State<TripQuestApp> createState() => _TripQuestAppState();
}

class _TripQuestAppState extends State<TripQuestApp> {
  late final AuthBloc _authBloc;
  late final GoRouter _router;

  @override
  void initState() {
    super.initState();
    _authBloc = sl<AuthBloc>()..add(const AppStarted());
    _router = GoRouter(
      initialLocation: '/splash',
      refreshListenable: _GoRouterRefreshStream(_authBloc.stream),
      redirect: (context, state) {
        final authState = _authBloc.state;
        final isAuthenticated = authState is AuthAuthenticated;
        final isUnauthenticated = authState is AuthUnauthenticated;
        final loc = state.matchedLocation;
        final isAuthRoute = loc == '/login' || loc == '/register';

        if (isUnauthenticated && !isAuthRoute && loc != '/splash') {
          return '/login';
        }
        if (isAuthenticated && (isAuthRoute || loc == '/splash')) {
          return '/globe';
        }
        return null;
      },
      routes: [
        GoRoute(
          path: '/splash',
          builder: (_, __) => const SplashPage(),
        ),
        GoRoute(
          path: '/login',
          builder: (_, __) => const LoginPage(),
        ),
        GoRoute(
          path: '/register',
          builder: (_, __) => const RegisterPage(),
        ),
        GoRoute(
          path: '/globe',
          builder: (_, __) => const GlobePage(),
        ),
      ],
    );
  }

  @override
  void dispose() {
    _authBloc.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return BlocProvider.value(
      value: _authBloc,
      child: MaterialApp.router(
        title: 'TripQuest',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
          useMaterial3: true,
        ),
        routerConfig: _router,
      ),
    );
  }
}

class _GoRouterRefreshStream extends ChangeNotifier {
  _GoRouterRefreshStream(Stream<AuthState> stream) {
    _subscription = stream.listen((_) => notifyListeners());
  }

  late final StreamSubscription<AuthState> _subscription;

  @override
  void dispose() {
    _subscription.cancel();
    super.dispose();
  }
}
