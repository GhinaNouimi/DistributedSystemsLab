# RUNNING GUIDE

## تشغيل المشروع

هذا الملف يشرح طريقة تشغيل مشروع Distributed Systems Lab وتجربة جميع الخوارزميات والـ Demos.

---

# الخطوة 1: تشغيل الخوادم

قبل تشغيل أي Demo أو Client يجب أولاً تشغيل:

```text
remote/RMIServerLauncher.java
```

إذا نجح التشغيل ستظهر رسالة مشابهة:

```text
RMI Registry started on port 1099
RMI Servers are ready...
```

وهذا يعني أن جميع الخوادم تم تسجيلها داخل RMI Registry.

---

# الخطوة 2: تجربة الخوارزميات المحلية

لتجربة الخوارزميات بدون RMI يمكن تشغيل:

```text
Main.java
```

سيتم تنفيذ:

* Round Robin
* Least Connections
* Health Aware
* Weighted Round Robin
* Power Of Two Choices
* Consistent Hashing

الغرض من هذا الملف هو توضيح طريقة عمل الخوارزميات بشكل بسيط قبل الانتقال إلى النسخة الموزعة.

---

# الخطوة 3: تجربة خوارزميات RMI

بعد تشغيل RMIServerLauncher يمكن تجربة أي Client من مجلد:

```text
demo
```

مثلاً:

```text
RMIRoundRobinClient
```

لاختبار Round Robin.

أو:

```text
RMILeastConnectionsClient
```

لاختبار Least Connections.

أو:

```text
RMIAdaptiveClient
```

لاختبار Adaptive Load Balancing.

---

# الخطوة 4: تجربة Heartbeat

شغّل:

```text
HeartbeatDemo
```

النتيجة المتوقعة:

```text
Healthy servers count = ...
```

وسيتم عرض الخوادم السليمة والخوادم المتوقفة.

---

# الخطوة 5: تجربة Data Sharding

شغّل:

```text
DataShardingDemo
```

سيتم توزيع مجموعة من المفاتيح مثل:

```text
patient-1001
appointment-2001
invoice-3001
```

على الخوادم السليمة باستخدام Consistent Hashing.

---

# الخطوة 6: تجربة Replication

شغّل:

```text
ReplicationDemo
```

هذا الـ Demo يختبر:

* Passive Replication
* Active Replication

ويظهر كيف يتم نسخ العمليات بين الخوادم.

---

# الخطوة 7: تجربة Fault Tolerance

شغّل:

```text
RMIFaultToleranceClient
```

سيتم اختبار:

* Retry
* Exponential Backoff
* Circuit Breaker
* Fallback

في حال فشل بعض الطلبات.

---

# سيناريوهات الفشل

تم إنشاء عدة Demos لمحاكاة الحالات الواقعية التي قد تحدث في الأنظمة الموزعة.

---

## PartialFailureDemo

اختبار حالة سقوط بعض الخوادم مع استمرار النظام بالعمل.

---

## AllServersDownDemo

اختبار حالة سقوط جميع الخوادم.

في هذه الحالة يجب أن يعطي النظام Fallback Response بدلاً من الانهيار.

ملاحظة:

بعد تشغيل هذا الـ Demo يجب إعادة تشغيل:

```text
RMIServerLauncher
```

لأن جميع الخوادم تصبح DOWN.

---

## RuntimeFailureDemo

اختبار سقوط خادم أثناء التشغيل.

يتم اكتشاف السقوط بواسطة Heartbeat ثم تتجاهله خوارزميات التوزيع.

---

## PassiveReplicationFailoverDemo

اختبار فشل الـ Leader وترقية Follower جديد ليصبح Leader.

---

## CircuitBreakerRecoveryDemo

اختبار حالات Circuit Breaker:

```text
CLOSED
OPEN
HALF_OPEN
```

ولإظهار هذه الحالة بشكل واضح يجب مؤقتاً جعل:

```text
ServerC failureRate = 100
```

داخل:

```text
RMIServerLauncher
```

ثم إعادة تشغيل السيرفر.

بعد انتهاء الاختبار يفضل إعادة القيمة الطبيعية.

---

## ShardingStabilityDemo

اختبار استقرار Consistent Hashing عند إضافة Server جديد.

النتيجة المتوقعة:

```text
Moved keys = 2 / 7
```

أو أي عدد صغير من المفاتيح.

هذا يثبت أن إضافة Server جديد لا تؤدي إلى إعادة توزيع جميع البيانات.

---

# ترتيب تشغيل مقترح

للعرض أمام المعيد يفضل تشغيل الملفات بالترتيب التالي:

```text
1. RMIServerLauncher
2. Main
3. HeartbeatDemo
4. DataShardingDemo
5. ReplicationDemo
6. RMIRoundRobinClient
7. RMILeastConnectionsConcurrentClient
8. RMIAdaptiveClient
9. RMIFaultToleranceClient
10. PartialFailureDemo
11. RuntimeFailureDemo
12. PassiveReplicationFailoverDemo
13. CircuitBreakerRecoveryDemo
14. ShardingStabilityDemo
15. AllServersDownDemo
```

ويفضل إبقاء:

```text
AllServersDownDemo
```

في النهاية لأنه يجعل جميع الخوادم DOWN.

---

# ملاحظات

إذا ظهر خطأ:

```text
Connection refused
```

أو:

```text
Registry not found
```

فهذا يعني أن:

```text
RMIServerLauncher
```

غير مشغّل.

يجب تشغيله أولاً ثم إعادة تشغيل الـ Demo المطلوب.
