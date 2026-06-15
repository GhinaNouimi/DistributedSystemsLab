# Distributed Systems Lab

## فكرة المشروع

هذا المشروع هو تطبيق عملي لمفاهيم مادة **Distributed Systems** باستخدام لغة **Java** وتقنية **Java RMI**.

الفكرة الأساسية من المشروع هي أنني لا أكتفي بشرح الخوارزميات نظرياً، بل أقوم ببنائها وتشغيلها وتجربة حالات النجاح والفشل عليها. حاولت أن يكون المشروع قريباً من فكرة الأنظمة الحقيقية التي تحتوي على عدة خوادم، وكل خادم يمكن أن يكون سليماً أو متوقفاً أو يفشل أثناء معالجة الطلبات.

المشروع يطبّق عدة مفاهيم مهمة مثل:

* Load Balancing
* Java RMI
* Heartbeat
* Fault Tolerance
* Retry and Backoff
* Circuit Breaker
* Fallback
* Data Sharding
* Consistent Hashing
* Active Replication
* Passive Replication
* Failover

---

# لماذا استخدمت Java RMI؟

استخدمت Java RMI لأن المشروع يحتاج إلى محاكاة نداءات بعيدة بين Client و Servers.

Java RMI تساعدنا على تنفيذ فكرة قريبة من:

```text
Remote Procedure Call - RPC
```

أي أن الـ Client يستدعي دالة موجودة داخل Server آخر وكأنها دالة محلية، لكن فعلياً يتم التواصل عبر الشبكة.

في المشروع، كل Server يتم تسجيله داخل:

```text
RMI Registry
```

ثم يستطيع الـ Client الوصول إليه عن طريق الاسم مثل:

```text
ServerA
ServerB
ServerC
```

---

# هيكلية المشروع

تم تقسيم المشروع إلى Packages واضحة حتى يكون كل جزء مسؤولاً عن فكرة محددة.

الهيكل العام:

```text
src
├── algorithms
├── core.model
├── demo
├── faulttolerance
├── heartbeat
├── loadbalancer
├── model
├── remote
├── replication
├── sharding
└── Main.java
```

هذا التقسيم ساعدنا على جعل الكود أوضح وأسهل للتعديل والتجربة.

---

# شرح Packages المشروع

## 1. package algorithms

هذا الـ package يحتوي النسخة المحلية من خوارزميات الـ Load Balancing.

المقصود بالنسخة المحلية أنها لا تستخدم RMI، وإنما تستخدم كائنات عادية من نوع:

```text
ServerNode
```

الهدف منها كان فهم الخوارزميات أولاً قبل ربطها مع RMI.

يحتوي على:

```text
RoundRobinLoadBalancer
LeastConnectionsLoadBalancer
HealthAwareLoadBalancer
WeightedRoundRobinLoadBalancer
PowerOfTwoChoicesLoadBalancer
ConsistentHashLoadBalancer
```

---

## RoundRobinLoadBalancer

هذه الخوارزمية توزع الطلبات بالتتابع بين الخوادم.

مثلاً لو لدينا:

```text
Server A
Server B
Server C
```

فالطلبات تذهب بهذا الشكل:

```text
Request 1 -> Server A
Request 2 -> Server B
Request 3 -> Server C
Request 4 -> Server A
```

هي خوارزمية بسيطة وسهلة، لكنها لا تراعي الفرق بين الخوادم من ناحية القوة أو عدد الطلبات الحالية.

---

## LeastConnectionsLoadBalancer

هذه الخوارزمية تختار الخادم الذي لديه أقل عدد من الاتصالات أو الطلبات النشطة.

مثلاً:

```text
Server A = 5 connections
Server B = 1 connection
Server C = 3 connections
```

فالطلب الجديد يذهب إلى:

```text
Server B
```

لأنه الأقل انشغالاً.

---

## HealthAwareLoadBalancer

هذه الخوارزمية لا تختار إلا الخوادم السليمة.

إذا كان لدينا:

```text
Server A = healthy
Server B = down
Server C = healthy
```

فالطلب لن يذهب إلى Server B.

---

## WeightedRoundRobinLoadBalancer

هذه الخوارزمية تشبه Round Robin، لكنها تراعي وزن الخادم.

مثلاً:

```text
Server A weight = 4
Server B weight = 2
Server C weight = 1
```

هذا يعني أن Server A يستقبل طلبات أكثر من Server C لأنه أقوى أو مخصص له وزن أعلى.

---

## PowerOfTwoChoicesLoadBalancer

هذه الخوارزمية تختار خادمين عشوائياً، ثم تختار الأقل انشغالاً بينهما.

الفكرة منها أنها تعطي نتائج جيدة بدون الحاجة إلى فحص كل الخوادم دائماً.

---

## ConsistentHashLoadBalancer

هذه الخوارزمية تستخدم Consistent Hashing لتوزيع المفاتيح أو المستخدمين على الخوادم.

ميزتها أن إضافة أو حذف خادم لا يؤدي إلى إعادة توزيع كل البيانات، وإنما جزء صغير فقط.

---

# 2. package model

هذا package يحتوي الكلاس:

```text
ServerNode
```

وهو يمثل الخادم في النسخة المحلية من المشروع.

يحتوي الخادم على:

* name
* weight
* activeConnections
* healthy

أي أن كل خادم لديه اسم ووزن وعدد اتصالات وحالة صحية.

---

# 3. package remote

هذا package يمثل الجزء الخاص بـ Java RMI.

يحتوي على:

```text
RemoteTaskService
RemoteTaskServiceImpl
RMIServerLauncher
```

---

## RemoteTaskService

هذا Interface يحدد العمليات التي يستطيع الـ Client استدعاءها عن بعد.

يحتوي على دوال مثل:

```text
processRequest()
getServerName()
isHealthy()
setHealthy()
startRequest()
finishRequest()
heartbeat()
```

---

## RemoteTaskServiceImpl

هذا هو التنفيذ الحقيقي للخادم.

كل Server في المشروع يستطيع:

* معالجة طلب
* إعطاء اسمه
* إرجاع حالته الصحية
* تغيير حالته من UP إلى DOWN أو العكس
* حساب عدد الطلبات النشطة
* حساب عدد الطلبات الناجحة والفاشلة
* حساب متوسط زمن الاستجابة
* الرد على heartbeat

كما أضفنا له:

```text
failureRate
```

حتى نستطيع محاكاة أن الخادم قد يفشل بنسبة معينة أثناء تنفيذ الطلب.

مثلاً:

```text
failureRate = 10%
```

يعني أن هناك احتمالاً أن يفشل الطلب حتى لو كان الخادم healthy.

---

## RMIServerLauncher

هذا الملف هو أول ملف يجب تشغيله في مشروع RMI.

وظيفته:

* إنشاء RMI Registry على port 1099
* إنشاء الخوادم
* تسجيلها بأسماء مثل ServerA و ServerB
* تحديد الحالة الصحية لكل Server
* تحديد الوزن
* تحديد failureRate

مثلاً:

```text
ServerA = healthy
ServerB = DOWN
ServerC = healthy
ServerD = DOWN
ServerE = healthy
```

بهذا نستطيع تجربة حالة الفشل الجزئي في النظام.

---

# 4. package loadbalancer

هذا package يحتوي نسخ RMI من خوارزميات توزيع الأحمال.

أي أنها لا تتعامل مع ServerNode المحلي، بل تتعامل مع:

```text
RemoteTaskService
```

الخوارزميات الموجودة:

```text
RMIRoundRobinLoadBalancer
RMILeastConnectionsLoadBalancer
RMIHealthAwareLoadBalancer
RMIPowerOfTwoChoicesLoadBalancer
RMIWeightedRoundRobinLoadBalancer
RMIAdaptiveLoadBalancer
```

---

## RMIRoundRobinLoadBalancer

يوزع الطلبات بالتتابع على الخوادم السليمة.

تم تحسينه حتى يتجاوز الخوادم المتوقفة ولا يرسل لها طلبات.

---

## RMILeastConnectionsLoadBalancer

يختار الخادم الذي لديه أقل عدد من الطلبات النشطة.

يستخدم:

```text
getActiveRequests()
```

ثم عند اختيار الخادم يتم استدعاء:

```text
startRequest()
```

وبعد انتهاء الطلب يجب على الـ Client استدعاء:

```text
finishRequest()
```

حتى يبقى عدد الطلبات النشطة صحيحاً.

---

## RMIHealthAwareLoadBalancer

يركز على حالة الخادم.

إذا كان الخادم DOWN يتم تجاوزه مباشرة.

---

## RMIPowerOfTwoChoicesLoadBalancer

يختار خادمين سليمين عشوائياً، ثم يختار الأقل انشغالاً بينهما.

---

## RMIWeightedRoundRobinLoadBalancer

يعتمد على أوزان الخوادم.

لكن إذا كان خادم معين DOWN يتم تجاوزه حتى لو كان وزنه عالياً.

---

## RMIAdaptiveLoadBalancer

هذه الخوارزمية هي الأكثر تطوراً في المشروع.

تحسب Score لكل خادم بناءً على:

```text
activeRequests
averageResponseTime
failedRequests
```

ثم تختار الخادم الذي لديه أقل Score.

الفكرة أن الخادم الذي يكون أسرع وأقل فشلاً وأقل انشغالاً يحصل على طلبات أكثر.

---

# 5. package heartbeat

يحتوي على:

```text
HeartbeatMonitor
```

هذا الكلاس يرسل heartbeat إلى كل خادم.

إذا رد الخادم بنجاح نعتبره healthy.

إذا رمى Exception نعتبره DOWN.

مثال:

```text
RMI Server A heartbeat OK
RMI Server B heartbeat FAILED -> DOWN
```

---

# 6. package faulttolerance

هذا package مسؤول عن التعامل مع فشل الطلبات.

يحتوي على:

```text
CircuitBreaker
CircuitBreakerState
FaultTolerantRequestExecutor
```

---

## CircuitBreaker

يطبق نمط Circuit Breaker.

الحالات هي:

```text
CLOSED
OPEN
HALF_OPEN
```

### CLOSED

النظام يسمح بإرسال الطلبات بشكل طبيعي.

### OPEN

النظام يمنع إرسال الطلبات إلى الخادم لأنه فشل عدة مرات.

### HALF_OPEN

بعد فترة انتظار، يسمح النظام بمحاولة واحدة لمعرفة هل تعافى الخادم أم لا.

---

## FaultTolerantRequestExecutor

هذا الكلاس يجمع عدة أفكار معاً:

* اختيار أفضل خادم متاح
* Retry عند الفشل
* Exponential Backoff
* Circuit Breaker
* Fallback Response

إذا فشل الطلب، يعيد المحاولة.

إذا فشل الخادم أكثر من مرة، يفتح Circuit Breaker.

إذا فشلت كل المحاولات، يعطي Fallback Response بدل أن ينهار النظام.

---

# 7. package sharding

هذا package مسؤول عن توزيع البيانات.

يحتوي على:

```text
ShardResolver
ConsistentHashShardResolver
```

---

## ShardResolver

هو Interface.

استخدمناه حتى يكون بإمكاننا تغيير طريقة توزيع البيانات لاحقاً بدون تعديل الكود الذي يستخدمه.

هذا يمثل Strategy Pattern.

---

## ConsistentHashShardResolver

يستخدم Consistent Hashing لتحديد أي خادم مسؤول عن key معين.

مثلاً:

```text
patient-1001 -> ServerE
invoice-3001 -> ServerC
```

استخدمنا Virtual Nodes لتحسين التوزيع.

الفكرة المهمة أن البيانات لا توزع على الخوادم المتوقفة، بل فقط على الخوادم السليمة.

---

# 8. package replication

هذا package مسؤول عن النسخ المتماثل.

يحتوي على:

```text
ActiveReplicationService
PassiveReplicationService
```

---

## PassiveReplicationService

يعتمد على فكرة:

```text
Leader - Followers
```

أي أن هناك Leader يعالج الطلب، ثم يتم نسخ العملية إلى Followers.

إذا فشل الـ Leader، يتم ترقية Follower جديد ليصبح Leader.

هذا يطبق فكرة Failover.

---

## ActiveReplicationService

في هذا النوع يتم إرسال نفس العملية إلى كل النسخ في الوقت نفسه.

أي أن كل Replica تنفذ نفس الطلب.

استخدمنا Threads حتى تتم العملية بشكل متوازي.

---

# 9. package demo

هذا package يحتوي كل التجارب العملية.

كل Demo يختبر سيناريو محدد.

---

## HeartbeatDemo

يختبر اكتشاف الخوادم السليمة والمتوقفة.

---

## DataShardingDemo

يختبر توزيع المفاتيح على الخوادم السليمة باستخدام Consistent Hashing.

---

## ReplicationDemo

يختبر:

* Passive Replication
* Active Replication

---

## PartialFailureDemo

يختبر حالة سقوط بعض الخوادم فقط.

مثلاً:

```text
ServerB = DOWN
ServerD = DOWN
```

ويثبت أن النظام يكمل العمل باستخدام الخوادم السليمة.

---

## AllServersDownDemo

يختبر حالة سقوط جميع الخوادم.

في هذه الحالة يجب ألا ينهار النظام، بل يعطي Fallback Response.

---

## RuntimeFailureDemo

يختبر سقوط خادم أثناء التشغيل.

أي أن الخادم يكون healthy في البداية، ثم يصبح DOWN أثناء تنفيذ البرنامج.

---

## PassiveReplicationFailoverDemo

يختبر فشل الـ Leader في Passive Replication.

إذا فشل Leader، يتم ترقية Follower جديد ويستمر النظام.

---

## CircuitBreakerRecoveryDemo

يختبر حالات Circuit Breaker.

لإظهار هذا السيناريو بوضوح جعلنا ServerC يفشل بنسبة 100% مؤقتاً.

بهذا نرى الانتقال:

```text
CLOSED -> OPEN -> HALF_OPEN -> OPEN
```

---

## ShardingStabilityDemo

يختبر استقرار Consistent Hashing.

عند إضافة Server جديد، لا تنتقل كل المفاتيح، بل جزء صغير فقط.

مثلاً:

```text
Moved keys = 2 / 7
```

وهذا يثبت أن Consistent Hashing يعمل بشكل صحيح.

---

Design & Architectural Patterns Used

1. Strategy Pattern
    - ShardResolver

2. Circuit Breaker Pattern
    - CircuitBreaker

3. Retry Pattern
    - FaultTolerantRequestExecutor

4. Fallback Pattern
    - FaultTolerantRequestExecutor

5. Leader-Follower Pattern
    - PassiveReplicationService

6. Service Discovery Concept
    - Java RMI Registry
   
# ماذا تعلمنا من المشروع؟

من خلال المشروع أصبح لدينا فهم عملي لعدة أفكار:

* كيف يتم توزيع الطلبات بين عدة خوادم
* كيف نكتشف الخادم المتوقف
* كيف نستبعد الخوادم المعطلة
* كيف نتعامل مع الفشل أثناء التشغيل
* كيف نعيد المحاولة عند فشل الطلب
* كيف يمنع Circuit Breaker تكرار الفشل
* كيف تعمل Fallback Response
* كيف يتم تقسيم البيانات بين الخوادم
* كيف يتم النسخ بين الخوادم
* كيف يتم Failover عند سقوط Leader
