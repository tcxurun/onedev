package com.pmease.commons.wicket.editable.polymorphic;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import com.pmease.commons.editable.PropertyDescriptor;
import com.pmease.commons.editable.PropertyDescriptorImpl;
import com.pmease.commons.editable.annotation.Editable;
import com.pmease.commons.util.ClassUtils;
import com.pmease.commons.wicket.editable.BeanContext;
import com.pmease.commons.wicket.editable.EditSupport;
import com.pmease.commons.wicket.editable.NotDefinedLabel;
import com.pmease.commons.wicket.editable.PropertyContext;
import com.pmease.commons.wicket.editable.PropertyEditor;

@SuppressWarnings("serial")
public class PolymorphicEditSuport implements EditSupport {

	@Override
	public BeanContext<?> getBeanEditContext(Class<?> beanClass) {
		return null;
	}

	@Override
	public PropertyContext<?> getPropertyEditContext(Class<?> beanClass, String propertyName) {
		PropertyDescriptor propertyDescriptpr = new PropertyDescriptorImpl(beanClass, propertyName);
		Class<?> propertyClass = propertyDescriptpr.getPropertyClass();
		if (propertyClass.getAnnotation(Editable.class) != null && !ClassUtils.isConcrete(propertyClass)) {
			return new PropertyContext<Serializable>(propertyDescriptpr) {

				@Override
				public Component renderForView(String componentId, IModel<Serializable> model) {
					if (model.getObject() != null)
						return new PolymorphicPropertyViewer(componentId, this, model.getObject());
					else
						return new NotDefinedLabel(componentId);
				}

				@Override
				public PropertyEditor<Serializable> renderForEdit(String componentId, IModel<Serializable> model) {
					return new PolymorphicPropertyEditor(componentId, this, model);
				}
				
			};
		} else {
			return null;
		}
	}

}
