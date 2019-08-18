package com.github.e13mort.codeview

class MutableCVClasses : CVClasses {
    private val classes = mutableListOf<CVClass>()

    companion object {
        fun of(cls: CVClass): CVClasses {
            return MutableCVClasses().apply { this.add(cls) }
        }
    }

    override fun add(aClass: CVClass) {
        classes.add(aClass)
    }

    override fun accept(visitor: CVClasses.Visitor) {
        classes.forEach { cvClass ->
            if (cvClass.has(ClassProperty.INTERFACE)) {
                visitor.onInterfaceDetected(cvClass)
            } else {
                visitor.onClassDetected(cvClass)
            }
        }
    }


}