package com.taskflow.presentation.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * 注册页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var agreeTerms by remember { mutableStateOf(false) }
    
    val auth: FirebaseAuth = Firebase.auth
    
    fun validateAndRegister() {
        if (email.isBlank() || !email.contains("@")) {
            errorMessage = "请输入有效的邮箱地址"
            return
        }
        if (password.length < 6) {
            errorMessage = "密码长度至少6位"
            return
        }
        if (password != confirmPassword) {
            errorMessage = "两次输入的密码不一致"
            return
        }
        if (!agreeTerms) {
            errorMessage = "请先同意用户协议和隐私政策"
            return
        }
        
        isLoading = true
        errorMessage = null
        
        MainScope().launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .await()
                onRegisterSuccess()
            } catch (e: Exception) {
                errorMessage = when {
                    e.message?.contains("email already in use") == true -> "该邮箱已被注册"
                    e.message?.contains("invalid email") == true -> "邮箱格式无效"
                    e.message?.contains("weak password") == true -> "密码强度不够"
                    else -> "注册失败: ${e.message}"
                }
            } finally {
                isLoading = false
            }
        }
    }
    
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // 标题
            Text(
                text = "创建账号",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = "开启TaskFlow之旅",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 邮箱输入
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    errorMessage = null
                },
                label = { Text("邮箱") },
                placeholder = { Text("请输入邮箱地址") },
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 密码输入
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    errorMessage = null
                },
                label = { Text("密码") },
                placeholder = { Text("至少6位字符") },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff 
                                         else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None 
                                       else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            
            // 密码强度指示器
            PasswordStrengthIndicator(password = password)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 确认密码输入
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    errorMessage = null
                },
                label = { Text("确认密码") },
                placeholder = { Text("请再次输入密码") },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff 
                                         else Icons.Filled.Visibility,
                            contentDescription = if (confirmPasswordVisible) "隐藏密码" else "显示密码"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None 
                                       else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 用户协议
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { 
                        agreeTerms = it
                        errorMessage = null
                    }
                )
                Text(
                    text = "我已阅读并同意",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { /* 打开用户协议 */ }) {
                    Text("用户协议", style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    text = "和",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { /* 打开隐私政策 */ }) {
                    Text("隐私政策", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 错误提示
            errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 注册按钮
            Button(
                onClick = { validateAndRegister() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("注册")
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 登录链接
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "已有账号？",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text("立即登录")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = when {
        password.length < 3 -> 0
        password.length < 6 -> 1
        password.length < 10 && password.matches(Regex(".*\\d.*")) -> 2
        password.length >= 10 && password.matches(Regex(".*\\d.*")) && 
            password.matches(Regex(".*[A-Z].*")) -> 3
        else -> 2
    }
    
    val colors = listOf(
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primary
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (0..3).forEach { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 2.dp)
                    .let { mod ->
                        if (index <= strength) {
                            mod.background(
                                colors.getOrElse(strength) { colors.last() },
                                shape = MaterialTheme.shapes.small
                            )
                        } else {
                            mod
                        }
                    }
            )
        }
    }
}
