package com.chenantao.autolayout.utils;

import android.view.ViewGroup;

/**
 * Created by Chenantao_gg on 2016/1/20.
 */
public class AutoLayoutGenerate
{
	public static <T extends ViewGroup> T generate(Class<T> clazz, Class[] argumentTypes, Object[]
			arguments)
	{
//		Enhancer enhancer = new Enhancer();
//		enhancer.setSuperclass(clazz);
//		CallbackFilter filter = new ConcreteClassCallbackFilter();
//		Callback methodInterceptor = new ConcreteClassMethodInterceptor<T>((Context) arguments[0],
//				(AttributeSet) arguments[1]);
//		Callback noOp = NoOp.INSTANCE;
//		Callback[] callbacks = new Callback[]{methodInterceptor, noOp};
//		enhancer.setCallbackFilter(filter);
//		enhancer.setCallbacks(callbacks);
//		T proxyObj = (T) enhancer.create(argumentTypes, arguments);
//		//对onMeasure方法以及generateLayoutParams进行拦截,其他方法不进行操作
//		return proxyObj;
		return null;
	}

//	static class ConcreteClassMethodInterceptor<T extends ViewGroup> implements MethodInterceptor
//	{
//		private AutoLayoutHelper mHelper;
//		private Context mContext;
//		private AttributeSet mAttrs;
//
//		public ConcreteClassMethodInterceptor(Context context, AttributeSet attrs)
//		{
//			mContext = context;
//			mAttrs = attrs;
//		}
//
//		public Object intercept(Object obj, Method method, Object[] arg, MethodProxy proxy)
//				throws Throwable
//		{
//			if (mHelper == null)
//			{
//				mHelper = new AutoLayoutHelper((ViewGroup) obj);
//			}
//			System.out.println("Before:" + method);
//			if ("onMeasure".equals(method.getName()))
//			{
//				//在onMeasure之前adjustChild
//				if (!((ViewGroup) obj).isInEditMode())
//				{
//					mHelper.adjustChildren();
//				}
//
//			} else if ("generateLayoutParams".equals(method.getName()))
//			{
//				ViewGroup parent = (ViewGroup) obj;
//				final T.LayoutParams layoutParams = (T.LayoutParams) Enhancer.create(T.LayoutParams
//								.class, new Class[]{
//								AutoLayoutHelper.AutoLayoutParams.class},
//						new MethodInterceptor()
//						{
//							public Object intercept(Object obj, Method method, Object[] args,
//							                        MethodProxy proxy) throws Throwable
//							{
//								if ("getAutoLayoutInfo".equals(method.getName()))
//								{
//									return AutoLayoutHelper.getAutoLayoutInfo(mContext, mAttrs);
//								}
//								return proxy.invoke(obj, args);
//							}
//						});
//				return layoutParams;
//			}
//			Object object = proxy.invokeSuper(obj, arg);
//			System.out.println("After:" + method);
//			return object;
//		}
//	}
//
//	static class ConcreteClassCallbackFilter implements CallbackFilter
//	{
//		public int accept(Method method)
//		{
//			if ("onMeasure".equals(method.getName()))
//			{
//				return 0;//Callback callbacks[0]
//			} else if ("generateLayoutParams".equals(method.getName()))
//			{
//				return 0;
//			}
//			return 1;
//		}
//	}
//	static class LayoutParamsGenerate implements FixedValue
//	{
//		public LayoutParamsGenerate(Context context, AttributeSet attributeSet)
//		{
//		}
//
//		public Object loadObject() throws Exception
//		{
//			System.out.println("ConcreteClassFixedValue loadObject ...");
//			Object object = 999;
//			return object;
//		}
//	}


}
