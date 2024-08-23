package com.hoangdoviet.furnitureshop.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.hoangdoviet.furnitureshop.firebase.FirebaseCommon
import com.hoangdoviet.furnitureshop.util.Constants.INTRODUCTION_SP
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // annotation được sử dụng để tạo các lớp module cung cấp dependencies.
@InstallIn(SingletonComponent::class)
// chỉ định sống của các phụ thuộc
//singletonComponet -> sống cả quá trình app
//activity hđ khi nào activity hđ
//servicecomponet, fragmentComponent

object AppModule {
    @Provides //Annotation này được sử dụng trong các module để chỉ định rằng một phương thức cung cấp một instance của dependency cụ thể.
    @Singleton//: Annotation này được sử dụng để chỉ định rằng chỉ có một instance của dependency sẽ được tạo và dùng chung trong toàn bộ phạm vi (scope) được xác định bởi component (ví dụ: ApplicationComponent).
    fun providerFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providerFirebaseFirestoreDatabase() = FirebaseFirestore.getInstance()

    @Provides
    // k dung Singleton => vì chỉ dùng cho fragment IntroductionFragment
    fun provideIntroductionSP(
        application: Application
    ) = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommon(firestore,firebaseAuth)
    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
}