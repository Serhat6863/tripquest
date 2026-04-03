import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import '../../../auth/presentation/bloc/auth_bloc.dart';
import '../../../auth/presentation/bloc/auth_event.dart';
import '../../../auth/presentation/bloc/auth_state.dart';

class GlobePage extends StatelessWidget {
  const GlobePage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocListener<AuthBloc, AuthState>(
      listener: (context, state) {
        if (state is AuthUnauthenticated) {
          context.go('/login');
        }
      },
      child: Scaffold(
        appBar: AppBar(
          title: const Text('TripQuest'),
          actions: [
            IconButton(
              icon: const Icon(Icons.logout),
              onPressed: () =>
                  context.read<AuthBloc>().add(const LogoutRequested()),
            ),
          ],
        ),
        body: const Center(
          child: Text(
            'Globe coming soon 🌍',
            style: TextStyle(fontSize: 20),
          ),
        ),
      ),
    );
  }
}
