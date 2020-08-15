package com.test.leakcache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ClassLoaderLeakExample {
    static volatile boolean running=true;

    public static void main(String[] args) throws InterruptedException, IOException {
        Thread thread=new Thread(new LongRunningThread());
        try{
            thread.start();
            System.out.println("Running, press any key to stop.");
            System.in.read();
        }finally{
            running=false;
            thread.join();
        }

    }

    static final class LongRunningThread implements Runnable{

        @Override
        public void run() {
            while(running){
                try {
                    loadAndDiscard();
                    Thread.sleep(100);
                } catch (Exception e) {
                    System.out.println("thread exception");
                    running=false;
                }

            }
        }
    }

    static void loadAndDiscard() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        ClassLoader childClassLoader=new ChildOnlyClassLoader();
        Class<?> childClass=Class.forName(LoadedInChildClassLoader.class.getName(),true,childClassLoader);
        childClass.newInstance();
    }

    static final class ChildOnlyClassLoader extends ClassLoader{
        ChildOnlyClassLoader(){
            super(ClassLoaderLeakExample.class.getClassLoader());
        }

        @Override
        protected Class<?> loadClass(String name,boolean resolve) throws ClassNotFoundException {
            if(!LoadedInChildClassLoader.class.getName().equals(name)){
                return super.loadClass(name,resolve);
            }
            try{
                Path path=Paths.get(LoadedInChildClassLoader.class.getName()+".class");
                byte[] classBytes= Files.readAllBytes(path);
                Class<?> c=defineClass(name,classBytes,0,classBytes.length);
                if(resolve){
                    resolveClass(c);
                }
                return c;
            }catch(Exception e){
                throw new ClassNotFoundException("Could not load "+name,e);
            }
        }

    }

    public static final class LoadedInChildClassLoader{
        static final byte[] moreBytesToLeeak=new byte[1024*1024*10];
        private static final ThreadLocal<LoadedInChildClassLoader> threeadLocal=new ThreadLocal<>();
        public LoadedInChildClassLoader(){
            threeadLocal.set(this);
        }
    }

}
