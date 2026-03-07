# Knowledge-Base 项目开发规则

**项目：** knowledge-base（Vue 3 + Spring Boot）
**创建日期：** 2026-03-07
**维护人：** 小克 🐕💎
**适用范围：** 所有针对 knowledge-base 项目的代码生成和开发工作

---

## 📋 规则概览

### 核心8条规则

1. ✅ Java 代码必须符合 Java 语法，不要混入 JS 风格
2. ✅ 使用第三方库必须同步更新 pom.xml
3. ✅ DTO 字段必须与方法一致
4. ✅ Controller 自动补全 import
5. ✅ API 地址统一使用 `/api` 前缀
6. ✅ 前端禁止写死 localhost API
7. ✅ 开发环境允许 CORS，生产环境建议 Nginx 代理

---

## 📝 详细规则

### 规则1：Java 语法规范 - 禁止 JS 风格代码

#### ❌ 错误示例

```java
// 错误：Java 的 replaceAll 不支持函数回调
.replaceAll("\\b\\w", String::valueOf(Character::toUpperCase))
```

#### ✅ 正确做法

```java
String[] words = text.split("\\s+");
StringBuilder result = new StringBuilder();

for (String word : words) {
    if (!word.isEmpty()) {
        result.append(Character.toUpperCase(word.charAt(0)));
        if (word.length() > 1) {
            result.append(word.substring(1));
        }
        result.append(" ");
    }
}
```

#### 原因说明

- Java 的 `replaceAll(String regex, String replacement)` 只支持字符串替换
- 不支持 JavaScript/Kotlin 风格的函数回调

#### ✅ 要求

未来生成 Java 代码时：
- **不要**使用 JS/Kotlin 风格的 replace callback
- 使用标准的 Java 字符串处理逻辑

---

### 规则2：第三方库依赖管理

#### ❌ 错误示例

**代码使用：**
```java
import org.apache.commons.lang3.StringUtils;

// 使用 StringUtils 的方法
StringUtils.isEmpty(text)
```

**但 pom.xml 缺失：**
```xml
<!-- 缺少这个依赖，导致编译错误 -->
```

#### ✅ 正确做法

**代码：**
```java
import org.apache.commons.lang3.StringUtils;
```

**pom.xml 必须包含：**
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.14.0</version>
</dependency>
```

#### ✅ 要求

如果生成代码引用第三方库：
- **必须**同步修改 `pom.xml`
- 添加对应的 `<dependency>` 声明
- 确保版本号正确

---

### 规则3：DTO 设计规范

#### ❌ 错误示例

**业务代码：**
```java
dto.setMessage("Hello");  // 调用 setMessage()
```

**DTO 类：**
```java
public class MyDTO {
    // 缺少 message 字段，导致编译错误
    // private String message;  // ❌ 这个字段缺失
}
```

#### ✅ 正确做法

**DTO 类必须完整：**
```java
public class MyDTO {
    private String message;  // ✅ 字段必须存在

    // Getter
    public String getMessage() {
        return message;
    }

    // Setter
    public void setMessage(String message) {
        this.message = message;
    }
}
```

#### ✅ 要求

未来生成 DTO 时必须保证：
- **字段**（Field）存在
- **Getter** 方法存在
- **Setter** 方法存在
- **字段名、方法名、业务逻辑一致**

---

### 规则4：Controller 注解自动补全

#### ❌ 错误示例

```java
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @CrossOrigin  // ❌ 缺少 import，编译错误
    @GetMapping
    public List<Note> getAll() {
        // ...
    }
}
```

#### ✅ 正确做法

```java
import org.springframework.web.bind.annotation.CrossOrigin;  // ✅ 补全 import
// 或者统一配置
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @CrossOrigin  // ✅ 不会编译错误
    @GetMapping
    public List<Note> getAll() {
        // ...
    }
}
```

#### ✅ 要求

生成 Controller 时：
- **必须**自动补全所有必要的 import
- 常用注解：`@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@CrossOrigin`
- 确保编译时不会出现 "cannot find symbol" 错误

---

### 规则5：Spring Boot 端口配置

#### ❌ 错误情况

```
Port 8080 was already in use
Tomcat started on port 8080 (http)
```

#### ✅ 解决方案

**方法1：修改配置文件**
```yaml
# application.yml
server:
  port: 8081  # 改为其他端口
```

**方法2：关闭占用进程**
```bash
# 查找占用 8080 的进程
lsof -i :8080
kill -9 <PID>
```

**方法3：命令行指定**
```bash
java -jar app.jar --server.port=8081
```

#### ✅ 要求

- 开发环境：如果端口冲突，可以改为 `8081`
- 生产环境：确保端口配置正确
- 避免多个应用占用同一端口

---

### 规则6：CORS 跨域配置

#### ❌ 错误情况

```
Access to XMLHttpRequest at 'http://localhost:8080/api/notes'
from origin 'http://127.0.0.1:8081' has been blocked by CORS policy:
No 'Access-Control-Allow-Origin' header is present
```

#### 原因说明

- 前端地址：`http://127.0.0.1:8081`
- 后端地址：`http://localhost:8080`
- 不同源 → 浏览器拦截

#### ✅ 解决方案（开发环境）

**方法1：单独 Controller 注解**
```java
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notes")
public class NoteController {
    // ...
}
```

**方法2：全局 CORS 配置（推荐）**
```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")  // 生产环境应该指定具体域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
```

#### ✅ 要求

- **开发环境**：可以使用 `allowedOrigins("*")` 简化配置
- **生产环境**：指定具体域名，如 `allowedOrigins("https://example.com")`
- 或者使用 Nginx 反向代理，避免跨域（见规则8）

---

### 规则7：前端 API 地址规范

#### ❌ 禁止写死

```javascript
// ❌ 错误：写死 localhost
const API_BASE = 'http://localhost:8080/api';

// ❌ 错误：写死 127.0.0.1
const API_BASE = 'http://127.0.0.1:8080/api';
```

#### ✅ 正确做法

**方式1：使用相对路径（推荐）**
```javascript
// API 基础路径
const API_BASE = '/api';

// 完整调用
axios.get('/api/notes')
  .then(response => { /* ... */ });
```

**方式2：环境变量配置**
```javascript
// .env.development
VITE_API_BASE=http://localhost:8080

// .env.production
VITE_API_BASE=/api

// 代码中
const API_BASE = import.meta.env.VITE_API_BASE;
```

**方式3：Nginx 反向代理（生产环境推荐）**
```javascript
// Nginx 配置后
const API_BASE = '/api';  // 不需要完整 URL
```

#### ✅ 要求

- 前端调用 API **必须**使用 `/api/xxx` 格式
- **不要**写死 `http://localhost:8080/api`
- 方便生产环境反向代理
- 避免 CORS 问题

---

### 规则8：生产环境部署架构

#### ❌ 不推荐

```
前端：http://example.com:3000
后端：http://example.com:8080
```
- 需要处理 CORS
- 两个端口，配置复杂

#### ✅ 推荐架构：Nginx 反向代理

```
example.com
 ├─ 静态文件（Vue 打包后的 dist）
  └─ /api → Spring Boot 后端
```

**Nginx 配置示例：**
```nginx
server {
    listen 80;
    server_name example.com;

    # 前端静态文件
    location / {
        root /var/www/knowledge-base/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 反向代理到 Spring Boot
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

#### ✅ 优点

- ✅ 统一域名和端口
- ✅ 避免 CORS 跨域问题
- ✅ Nginx 处理静态文件，性能更好
- ✅ 后端可以内网部署，更安全

#### ✅ 要求

- **生产环境**：使用 Nginx 反向代理
- **前端**：`dist` 目录部署到 Nginx root
- **后端**：`/api` 路径代理到 Spring Boot
- **避免 CORS**：通过反向代理统一域名

---

## 🎯 快速检查清单

### 代码生成时自检

生成 Java/后端代码前：
- [ ] 是否符合 Java 语法（无 JS 风格）
- [ ] 使用第三方库是否更新 pom.xml
- [ ] DTO 字段、Getter、Setter 是否完整
- [ ] Controller 是否补全所有 import

生成前端代码前：
- [ ] API 地址是否使用 `/api` 前缀
- [ ] 是否写死 localhost（禁止）
- [ ] 是否使用相对路径

部署配置前：
- [ ] 开发环境是否配置 CORS
- [ ] 生产环境是否使用 Nginx 代理
- [ ] API 路径是否统一为 `/api`

---

## 📚 相关资源

### Java 文档
- [Java String 文档](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)

### 前端文档
- [Vue 3 官方文档](https://vuejs.org/)
- [Axios 文档](https://axios-http.com/)
- [Element Plus 文档](https://element-plus.org/)

### 部署文档
- [Nginx 反向代理](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)
- [Spring Boot CORS 配置](https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html)

---

## 🔄 更新历史

| 日期 | 更新内容 | 更新人 |
|------|----------|--------|
| 2026-03-07 | 初始创建，记录8条核心规则 | 小克 🐕💎 |

---

## 💡 备注

- 本规则适用于 knowledge-base 项目
- 其他项目可以参考，但需根据实际情况调整
- 遇到新问题时，更新本文档

---

**文档版本：** v1.0
**最后更新：** 2026-03-07
**维护者：** 小克 🐕💎
