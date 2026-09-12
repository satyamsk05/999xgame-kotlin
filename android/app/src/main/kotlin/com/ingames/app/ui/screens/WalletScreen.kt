package com.ingames.app.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun WalletScreen(
    onAddCashClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onTransactionsClick: () -> Unit,
    onSupportClick: () -> Unit,
    onBackClick: () -> Unit
) {
    ProfileScreen(
        onSettingsClick = onSupportClick,
        onHelpClick = onSupportClick,
        onFairPlayClick = {},
        onContactClick = onSupportClick,
        onLogoutClick = onBackClick,
        onBackClick = onBackClick,
        onAddCashClick = onAddCashClick,
        onWithdrawClick = onWithdrawClick,
        onTransactionsClick = onTransactionsClick
    )
}
