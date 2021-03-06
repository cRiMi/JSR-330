/**
 * @author julien
 */
package com.serli.jderay.jsr330;

import com.serli.jderay.jsr330.exceptions.*;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;

public class ContainerConfig {

    public ContainerConfig() {
    }

    public void configure() throws AmbiguousImplementationsException, DoesNotImplementException, NotAnInterfaceException, NoImplementationException, IsNotScopeException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, MultipleConstructorsInjection, NoSuchFieldException, FinalFieldException {
    }

    public BindedClass bind(Class<?> clazz) throws NotAnInterfaceException {
        return new BindedClass(clazz);
    }

    public class QualifieredClass {

        Class<?> clazzToImpl;
        Class<?>[] qualifiers;
        String name;

        public QualifieredClass(Class<?> clazzToImpl, Class<?>[] qualifiers) {
            this.clazzToImpl = clazzToImpl;
            this.qualifiers = qualifiers;
            this.name = "";
        }

        public QualifieredClass(Class<?> clazzToImpl, String name, Class<?>[] qualifiers) {
            this( clazzToImpl, qualifiers );
            this.name = name;
        }
   
        public AfterTo to(Class<?> clazzImpl) {
            return new AfterTo( clazzToImpl, clazzImpl, qualifiers, name );
        }

        // TODO : test si clazzToImpl == clazzImpl
        public void providedBy(Provider provider) throws AmbiguousImplementationsException, InstantiationException, NoImplementationException, IllegalAccessException, NoSuchMethodException {
            Class<?> clazzImpl = provider.getClass().getMethod("get", null).getReturnType();

            if ( qualifiers.length > 0 ) {
                InheritanceManager.addInheritance(clazzToImpl, clazzImpl, name, qualifiers);
                InheritanceManager.setProvider(clazzToImpl, name, qualifiers);
            }

            InheritanceManager.addProvider(clazzImpl, provider);
        }

    }

    public class AfterTo {

        Class<?> clazzToImpl;
        Class<?>[] qualifiers;
        String name;
        
        public AfterTo(Class<?> clazzToImpl, Class<?> clazzImpl) {
            this.clazzToImpl = clazzToImpl;
            this.qualifiers = null;
            this.name = "";
            
            InheritanceManager.addInheritance(clazzToImpl, clazzImpl);
        }

        public AfterTo(Class<?> clazzToImpl, Class<?> clazzImpl, Class<?>[] qualifiers, String name) {
            this.clazzToImpl = clazzToImpl;
            this.qualifiers = qualifiers;
            this.name = name;
            
            InheritanceManager.addInheritance(clazzToImpl, clazzImpl, name, qualifiers);
        }
        
        public void withScope( Class<?> singleton ) throws IsNotScopeException, InstantiationException, IllegalAccessException, NoImplementationException, AmbiguousImplementationsException, NoSuchMethodException, MultipleConstructorsInjection, FinalFieldException, NoSuchFieldException, InvocationTargetException {
            if ( singleton.equals( Singleton.class ) )
                InheritanceManager.addSingletonToPostConfigList(clazzToImpl, qualifiers, name);
            else
                throw new IsNotScopeException( singleton.getName() );
        }
    }

    public class BindedClass {

        Class<?> clazzToImpl;

        public BindedClass(Class<?> clazz) {
            this.clazzToImpl = clazz;
        }

        public AfterTo to(Class<?> implementation) throws DoesNotImplementException, NoImplementationException {
            if ( isAnImplementation( implementation ) )
                return new AfterTo( clazzToImpl, implementation );
            else 
                throw new NoImplementationException( implementation.getName() );
        }

        public QualifieredClass annotatedWith(Class<?>... qualifiers) {
            return new QualifieredClass( clazzToImpl, qualifiers );
        }

        private boolean isAnImplementation(Class<?> implementation) throws DoesNotImplementException {
            if ( clazzToImpl.isAssignableFrom( implementation ) )
                return true;
            else
                throw new DoesNotImplementException( clazzToImpl.getName(), implementation.getName() );
        }

        public QualifieredClass named(String name) {
            Class<?>[] qualifiers = {Named.class};
            return new QualifieredClass( clazzToImpl, name, qualifiers );
        }

        // TODO : test si clazzToImpl == clazzImpl
        public void providedBy(Provider provider) throws NoSuchMethodException, NoImplementationException, AmbiguousImplementationsException {
            Class<?> clazzImpl = provider.getClass().getMethod("get", null).getReturnType();
            //InheritanceManager.addInheritance(clazzToImpl, clazzImpl, "", nullQualifier);
            InheritanceManager.addProvider(clazzImpl, provider);
        }
    }
}
