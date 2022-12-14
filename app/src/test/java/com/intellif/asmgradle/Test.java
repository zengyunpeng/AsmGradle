package com.intellif.asmgradle;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM4;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Test {

    public static void createClass() throws IOException {
        //新建一个类生成器，COMPUTE_FRAMES，COMPUTE_MAXS这2个参数能够让asm自动更新操作数栈
        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        //生成一个public的类，类路径是com.study.Human
        cw.visit(V1_8, ACC_PUBLIC, "com/intellif/asmgradle/Human", null, "java/lang/Object", null);

        //生成默认的构造方法： public Human()
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);//更新操作数栈
        mv.visitEnd();//一定要有visitEnd

        //生成成员变量
        //1.生成String类型的成员变量:private String name;
        FieldVisitor fv = cw.visitField(ACC_PRIVATE, "name", "Ljava/lang/String;", null, null);
        fv.visitEnd();//不要忘记end
        //2.生成Long类型成员：private long age
        fv = cw.visitField(ACC_PRIVATE, "age", "J", null, null);
        fv.visitEnd();

        //3.生成Int类型成员:protected int no
        fv = cw.visitField(ACC_PROTECTED, "no", "I", null, null);
        fv.visitEnd();

        //4.生成静态成员变量：public static long score
        fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, "score", "J", null, null);

        //5.生成常量：public static final String real_name = "Sand哥"
        fv = cw.visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, "real_name", "Ljava/lang/String;", null, "Sand哥");
        fv.visitEnd();

        //6.生成成员方法greet
        mv = cw.visitMethod(ACC_PUBLIC, "greet", "(Ljava/lang/String;)I", null, null);
        mv.visitCode();
        mv.visitIntInsn(ALOAD, 0);
        mv.visitIntInsn(ALOAD, 1);

        //6.1 调用静态方法 System.out.println("Hello");
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//        mv.visitLdcInsn("Hello");//加载字符常量
        mv.visitIntInsn(ALOAD, 1);//加载形参
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);//打印形参
        //6.2 创建局部变量
        LocalVariablesSorter lvs = new LocalVariablesSorter(ACC_PUBLIC, "(Ljava/lang/String;)I", mv);
        //创建ArrayList 对象
        //new ArrayList ,分配内存不初始化
        mv.visitTypeInsn(NEW, "java/util/ArrayList");
        mv.visitInsn(DUP);//压入栈
        //弹出一个对象所在的地址，进行初始化操作，构造函数默认为空，此时栈大小为1（到目前只有一个局部变量）
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);

        int time = lvs.newLocal(Type.getType(List.class));

        mv.visitVarInsn(ASTORE, time);
        mv.visitVarInsn(ALOAD, time);

        //创建StringBuilder对象
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

        //这里需要注意在lvs.newLocal的时候使用Type.geteType("类路径") 会报错，需要改成Type.geteType("XXX.class“)的方式
        time = lvs.newLocal(Type.getType(StringBuilder.class));
        mv.visitVarInsn(ASTORE, time);
        mv.visitVarInsn(ALOAD, time);

        mv.visitLdcInsn("Hello java asm StringBuilder");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        time = lvs.newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(LSTORE, time);
        mv.visitLdcInsn(10);
        mv.visitLdcInsn(11);
        mv.visitInsn(IADD);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        //生成静态方法
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "staticMethod", "(Ljava/lang/String;)V", null, null);
        //生成静态方法中的字节码指令
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Hello Java Asm!");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        cw.visitEnd();

        String apth =
                "D:\\AsWorkSpace\\AsmGradle\\app\\src\\test\\java\\com\\intellif\\asmgradle\\" + "Human.class";
        byte[] bytes = cw.toByteArray();
        File file = new File(apth);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        boolean writeFileFromBytesByChannel =
                FileIOUtils.writeFileFromBytesByChannel(file, bytes, true);
        System.out.println("文件生成: " + writeFileFromBytesByChannel);
    }

    @org.junit.Test
    public void modifyClass() throws IOException {
        ClassReader cr = new ClassReader("com.intellif.asmgradle.OriginalClass");
        final ClassWriter cw = new ClassWriter(cr, 0);

        cr.accept(new ClassVisitor(ASM4, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//                if("testA".equals(name)){//拷贝的过程中删除一个方法
//                    return null;
//                }
//
//                if("testC".equals(name)){//将testC public方法变成protect
//                    access=ACC_PROTECTED;
//                }
                MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);


                return new MethodVisitor(ASM4, mv) {
                    @Override
                    public void visitLineNumber(int line, Label start) {
                        System.out.println("经过这个测试行数:" + line + "可以对应到源代码的行数");
                        super.visitLineNumber(line, start);
                    }



                    @Override
                    public void visitInsn(int opcode) {
//                        super.visitInsn(opcode);
                        //这里是访问语句结束，在return结束之前添加语句
                        //其中的 owner 必须被设定为所转换类的名字。现在必须在
                    }

                    @Override
                    public void visitCode() {
                        super.visitCode();
//                        //方法开始（可以在此处添加代码，在原来的方法之前执行）
                        if ("i".equals(name)) {
                            System.out.println("visitCode==>\"i\".equals(name)");
                            MethodVisitor methodVisitor = cv.visitMethod(ACC_PUBLIC, "i", "(Ljava/lang/String;Ljava/lang/String;)V", null, null);
                            methodVisitor.visitCode();
                            Label label0 = new Label();
                            methodVisitor.visitLabel(label0);
                            methodVisitor.visitLineNumber(13, label0);
                            methodVisitor.visitFieldInsn(GETSTATIC, "com/intellif/asmgradle/OriginalClassV2", "tagList", "Ljava/util/List;");
                            methodVisitor.visitVarInsn(ALOAD, 1);
                            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "contains", "(Ljava/lang/Object;)Z", true);
                            Label label1 = new Label();
                            methodVisitor.visitJumpInsn(IFEQ, label1);
                            Label label2 = new Label();
                            methodVisitor.visitLabel(label2);
                            methodVisitor.visitLineNumber(14, label2);
                            methodVisitor.visitInsn(RETURN);
                            methodVisitor.visitLabel(label1);
                            methodVisitor.visitLineNumber(18, label1);
                            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            methodVisitor.visitInsn(RETURN);
                            Label label3 = new Label();
                            methodVisitor.visitLabel(label3);
                            methodVisitor.visitLocalVariable("this", "Lcom/intellif/asmgradle/OriginalClassV2;", null, label0, label3, 0);
                            methodVisitor.visitLocalVariable("tag", "Ljava/lang/String;", null, label0, label3, 1);
                            methodVisitor.visitLocalVariable("content", "Ljava/lang/String;", null, label0, label3, 2);
                            methodVisitor.visitMaxs(2, 3);
                            methodVisitor.visitEnd();
                        }


                    }


                    @Override
                    public void visitEnd() {
                        //特别注意的是：要为类增加属性和方法，放到visitEnd中，避免破坏之前已经排列好的类结构，在结尾添加新结
                        //增加一个字段（注意不能重复）,注意最后都要visitEnd

                        //静态方法里面给tag集合添加数据   TODO
//                        MethodVisitor methodVisitor = cv.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);


                        super.visitEnd();

                    }
                };
            }


            @Override
            public void visitEnd() {
                System.out.println("-----------visitEnd-------------------");
                FieldVisitor fieldVisitor = cv.visitField(ACC_PUBLIC | ACC_STATIC, "tagList", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/String;>;", null);
                fieldVisitor.visitEnd();

                //生成静态方法
                {
                    MethodVisitor methodVisitor = cv.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
                    methodVisitor.visitCode();
                    Label label0 = new Label();
                    methodVisitor.visitLabel(label0);
                    methodVisitor.visitLineNumber(9, label0);
                    methodVisitor.visitFieldInsn(GETSTATIC, "com/intellif/asmgradle/OriginalClass", "tagList", "Ljava/util/List;");
                    methodVisitor.visitLdcInsn("KKYS");
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
                    methodVisitor.visitInsn(POP);
                    Label label1 = new Label();
                    methodVisitor.visitLabel(label1);
                    methodVisitor.visitLineNumber(10, label1);
                    methodVisitor.visitInsn(RETURN);
                    methodVisitor.visitMaxs(2, 0);
                    methodVisitor.visitEnd();
                }


                super.visitEnd();

            }
        }, 0);


        String apth =
                "D:\\AsWorkSpace\\AsmGradle\\app\\src\\test\\java\\com\\intellif\\asmgradle\\" + "OriginalClass2.class";
        byte[] bytes = cw.toByteArray();
        File file = new File(apth);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        boolean writeFileFromBytesByChannel =
                FileIOUtils.writeFileFromBytesByChannel(file, bytes, true);
        System.out.println("文件生成: " + writeFileFromBytesByChannel);

    }

}
