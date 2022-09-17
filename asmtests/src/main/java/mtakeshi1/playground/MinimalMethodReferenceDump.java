package mtakeshi1.playground;

import org.objectweb.asm.*;

import java.lang.invoke.*;

public class MinimalMethodReferenceDump implements Opcodes {

    public static Runnable bla() {
        return new Runnable() {
            @Override
            public void run() {
                System.out.println("inner bla");
            }
        };
    }

    public  CallSite localBoot(MethodHandles.Lookup caller, String name, MethodType type) {
        return new ConstantCallSite(MethodHandles.constant(Runnable.class, bla()));
    }

    public static CallSite myBoot(MethodHandles.Lookup caller, String name, MethodType type, Object... args) {
//        return null;
        MethodHandle arg = (MethodHandle) args[1];
        MethodHandleInfo delegateInfo = caller.revealDirect(arg);
        ConstantCallSite site = new ConstantCallSite(arg);
//        throw new RuntimeException();
//        return site;
        return new ConstantCallSite(MethodHandles.constant(Runnable.class, bla()));
    }

    public static CallSite myBoot2(MethodHandles.Lookup caller, String name, MethodType type, Object... args) {
//        return null;
        MethodHandle arg = (MethodHandle) args[1];
        MethodHandleInfo delegateInfo = caller.revealDirect(arg);
        ConstantCallSite site = new ConstantCallSite(arg);
//        throw new RuntimeException();
//        return site;
        return new ConstantCallSite(MethodHandles.constant(Runnable.class, bla()));
    }

    public static void runOnBehalf(Object target) {
        System.out.println("worked!");
    }

    public static byte[] dump() throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, "MinimalMethodReference", null, "java/lang/Object", new String[]{"java/lang/Runnable"});

        classWriter.visitSource("MinimalMethodReference.java", null);

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(3, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "LMinimalMethodReference;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(6, label0);
//            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            methodVisitor.visitInsn(DUP);
            //  public static CallSite myBoot(Lookup caller, String name, MethodType type, Object... args) {
            Type[] argTypes = new Type[] {
                    Type.getType(MethodHandles.Lookup.class),
                    Type.getType(String.class),
                    Type.getType(MethodType.class),
                    Type.getType(Object[].class)

            };
            String owner = Type.getInternalName(MinimalMethodReferenceDump.class);
            methodVisitor.visitInvokeDynamicInsn(
                    "run", // not sure what this is for. Maybe just communicating where to extract the MH from?
//                    "(Ljava/io/PrintStream;)Ljava/lang/Runnable;", //operands are extracted from here
                    "()Ljava/lang/Runnable;", // the bootstrap should return a Callsite with a 'type' that matches this signature.

//                    new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
                    new Handle(Opcodes.H_INVOKESTATIC, owner, "myBoot2", Type.getMethodDescriptor(Type.getType(CallSite.class), argTypes), false),

                    Type.getType("()V"),  //MethodType interfaceMethodType,

                    //new Handle(Opcodes.H_INVOKEVIRTUAL, "java/io/PrintStream", "println", "()V", false), // MethodHandle implementation,
                    new Handle(Opcodes.H_INVOKESTATIC, owner, "runOnBehalf", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class)), false), // MethodHandle implementation,

                    Type.getType("()V"), 1); //MethodType dynamicMethodType)

            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(7, label1);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/lang/Runnable", "run", "()V", true);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(8, label2);
            methodVisitor.visitInsn(RETURN);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLocalVariable("this", "LMinimalMethodReference;", null, label0, label3, 0);
            methodVisitor.visitLocalVariable("run", "Ljava/lang/Runnable;", null, label1, label3, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        byte[] bytecode = dump();
        OpenClassLoader cl = new OpenClassLoader();
        cl.addClass("MinimalMethodReference", bytecode);
        Class<?> aClass = cl.loadClass("MinimalMethodReference");
        Runnable run = (Runnable) aClass.getConstructor().newInstance();
        run.run();
    }

}
