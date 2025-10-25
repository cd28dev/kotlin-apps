# FaceNet Model Placeholder
# 
# Este archivo representa donde debería estar el modelo TensorFlow Lite para FaceNet
# 
# INSTRUCCIONES PARA OBTENER UN MODELO REAL:
# 
# 1. Descargar modelo pre-entrenado de FaceNet/MobileFaceNet:
#    - https://github.com/sirius-ai/MobileFaceNet_TF
#    - https://github.com/davidsandberg/facenet
#    - https://www.tensorflow.org/lite/models/face/overview
# 
# 2. Convertir a TensorFlow Lite si es necesario:
#    - Usar tensorflow.lite.TFLiteConverter
#    - Optimizar para mobile con quantization
# 
# 3. Colocar el archivo .tflite aquí con nombre:
#    - facenet_mobile.tflite (recomendado)
#    - mobilefacenet.tflite
# 
# 4. El modelo debe:
#    - Entrada: imagen facial 112x112 o 160x160
#    - Salida: vector embedding 128 o 512 dimensiones
#    - Formato: float32 normalizado
# 
# NOTA: Por ahora usaremos un generador de embeddings simulado
# que cumple con la interfaz requerida para desarrollo/testing

# Modelo temporal - REEMPLAZAR con modelo real
MODELO_TEMPORAL = True

# →  https://github.com/syaringan357/Android-MobileFaceNet-MTCNN-FaceAntiSpoofing/blob/master/README.md