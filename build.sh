#!/usr/bin/env bash
# Build WenShu APK from Kotlin source (no Gradle, no Android Studio)
set -euo pipefail

ANDROID_SDK=/home/lwx/Android/Sdk
PKG=com.ciel.hsk

cd "$(dirname "$0")"

echo "=== Cleaning build artifacts ==="
rm -rf build/compiled build/R_gen build/classes_java build/classes_kotlin build/dex build/app_linked.apk build/app_unsigned.apk build/hsk-app.apk
mkdir -p build/compiled build/R_gen build/classes_java build/classes_kotlin build/dex

echo "=== Step 1: aapt2 compile ==="
find app/src/main/res -type f | while read f; do
    "$ANDROID_SDK/build-tools/34.0.0/aapt2" compile -o build/compiled/ "$f"
done

echo "=== Step 2: aapt2 link ==="
"$ANDROID_SDK/build-tools/34.0.0/aapt2" link -o build/app_linked.apk \
    -I "$ANDROID_SDK/platforms/android-33/android.jar" \
    --manifest app/src/main/AndroidManifest.xml \
    --java build/R_gen/ \
    $(find build/compiled -type f)

echo "=== Step 3: javac R.java ==="
javac -source 8 -target 8 \
    -cp "$ANDROID_SDK/platforms/android-33/android.jar" \
    -d build/classes_java \
    build/R_gen/com/ciel/hsk/R.java

echo "=== Step 4: kotlinc-jvm ==="
build/kotlinc/bin/kotlinc-jvm \
    -cp "$ANDROID_SDK/platforms/android-33/android.jar:build/classes_java:build/kotlin-stdlib-2.4.21.jar" \
    -d build/classes_kotlin \
    app/src/main/kotlin/com/ciel/hsk/*.kt

echo "=== Step 5: d8 ==="
CLASS_FILES=$(find build/classes_kotlin build/classes_java -name "*.class")
"$ANDROID_SDK/build-tools/34.0.0/d8" --release \
    --lib "$ANDROID_SDK/platforms/android-33/android.jar" \
    --output build/dex/ \
    $CLASS_FILES \
    build/kotlin-stdlib-2.4.21.jar

echo "=== Step 6: package ==="
cp build/app_linked.apk build/app_unsigned.apk
python3 -c "
import zipfile, os
with zipfile.ZipFile('build/app_unsigned.apk', 'a') as z:
    z.write('build/dex/classes.dex', 'classes.dex')
    for root, dirs, files in os.walk('app/src/main/assets'):
        for f in files:
            full = os.path.join(root, f)
            arc = os.path.relpath(full, 'app/src/main/assets')
            z.write(full, arc)
print('classes.dex + assets added')
"

echo "=== Step 7: sign ==="
if [[ ! -f build/hsk.keystore ]]; then
    keytool -genkeypair -keystore build/hsk.keystore -alias hsk \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -storepass android -keypass android \
        -dname "CN=WenShu Development, OU=Development, O=WenShu, C=US"
fi
"$ANDROID_SDK/build-tools/34.0.0/apksigner" sign \
    --ks build/hsk.keystore \
    --ks-key-alias hsk \
    --ks-pass pass:android \
    --key-pass pass:android \
    --out build/hsk-app.apk \
    build/app_unsigned.apk

echo "=== Step 8: verify ==="
"$ANDROID_SDK/build-tools/34.0.0/apksigner" verify --verbose build/hsk-app.apk
"$ANDROID_SDK/build-tools/34.0.0/aapt2" dump badging build/hsk-app.apk | sed -n '1,14p'

echo ""
echo "=== BUILD COMPLETE ==="
echo "APK: build/hsk-app.apk"
echo "Install: adb install -r build/hsk-app.apk"
